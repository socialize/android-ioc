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

import android.util.Log;

/**
 * @author Jason
 */
public final class ThreadLock  {

	private volatile boolean locked;
	private volatile long locker;
	
	public final synchronized void lock() {
		long tid = Thread.currentThread().getId();
		if(!locked) {
			locker = tid;
			locked = true;
		}
		else if(locker != tid) {
			try {
				wait();
				// try again
				lock();
			} catch (InterruptedException ignore) {}
		}
	}
	
	public final synchronized void await() {
		await(true);
	}
	
	public final synchronized void await(boolean includeMe) {
		long tid = Thread.currentThread().getId();
		if(!locked || (includeMe && locker != tid)) {
			return;
		}
		else {
			try {
				wait();
				// try again
				await(includeMe);
			} catch (InterruptedException ignore) {}
		}
	}	
	
	/**
	 * Forcibly releases all locks
	 */
	public final synchronized void release() {
		locked = false;
		locker = -1;
		notifyAll();
	}

	public final synchronized void unlock() {
		if(locked) {
			if(locker == Thread.currentThread().getId()) {
				locked = false;
				locker = -1;
				notifyAll();
			}
			else {
				Log.w(getClass().getSimpleName(), "Thread " + Thread.currentThread().getName() + " attempted to unlock but is not lock owner!");
			}
		}
	}
	
	public final synchronized void unlockAll() {
		if(locked) {
			locked = false;
			locker = -1;
			notifyAll();
		}
	}
}
