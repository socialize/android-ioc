/*
 * Copyright (c) 2012 Socialize Inc. 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.android.ioc;

import java.lang.reflect.Constructor;
import android.app.Activity;
import android.content.Context;
import android.os.Looper;

/**
 * Ensures objects are allocated on the main UI thread where possible.
 * @author Jason Polites
 *
 */
public class SafeAllocator implements Allocator {
	
	boolean running = false;

	final ThreadLock lock = new ThreadLock();
	final Object waitLock = new Object();
	Allocator allocator;
	
	public SafeAllocator() {
		super();
		running = true;
		allocator = new Allocator();
		allocator.setDaemon(true);
		allocator.start();
	}

	/* (non-Javadoc)
	 * @see com.socialize.android.ioc.Allocator#allocate(android.content.Context, java.lang.reflect.Constructor, java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Object> T allocate(Context context, Constructor<T> constructor, Object...args) throws Exception {
		boolean onUIThread = onUIThread();
		if(!onUIThread && context instanceof Activity) {
			return (T) allocateOnUIThread(((Activity)context), constructor, args);
		}
		else {
			return constructor.newInstance(args);
		}
	}
	
	private boolean onUIThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}
	
	class Allocator extends Thread {
		
		Activity activity;
		Constructor<?> constructor;
		Object[] args;
		Object result;
		boolean job = false;
		
		@Override
		public void run() {
			while(running) {
				synchronized (waitLock) {
					try {
						waitLock.wait();
					}
					catch (InterruptedException ignore) {}
					
					if(running) {
						if(job) {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										result = constructor.newInstance(args);
									}
									catch (Exception e) {
										result = e;
									}
									finally {
										lock.release();
									}
								}
							});
						}	
					}
				}
			}
		};
	};

	
	Object allocateOnUIThread(Activity activity, Constructor<?> constructor, Object...args) throws Exception {
		lock.lock();
		allocator.result = null;
		allocator.activity = activity;
		allocator.args = args;
		allocator.constructor = constructor;
		allocator.job = true;
		synchronized(waitLock) {
			waitLock.notifyAll();
		}
		lock.await();
		
		if(allocator.result instanceof Exception) {
			throw (Exception) allocator.result;
		}
		
		return allocator.result;
	}
		
	
	@Override
	protected void finalize() throws Throwable {
		running = false;
		allocator.job = false;
		synchronized(waitLock) {
			waitLock.notifyAll();
		}
		super.finalize();
	}
}
