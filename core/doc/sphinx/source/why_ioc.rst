.. include:: header.inc

.. _why:		
	
============================
Why use AndroidIOC?
============================

Why you should use Dependency Injection
---------------------------------------


AndroidIOC vs. RoboGuice
------------------------

While the folks at RoboGuice have done a fantastic job of making wrapping the Google Guice dependency injection system to make it compatible for Android, there
are several reasons where AndroidIOC differs.  This is not to say that AndroidIOC is "better" or "worse", simply different and each developer will need to assess 
the pros and cons of both to make a final decision.

Having said that, here are some of the key differences:

1. AndroidIOC is a lightweight framework which does not involve itself in your class heirarchy.

   One of the requirement of RoboGuice is that all of your Activities, Services, Broadcast Receivers etc extend a RoboGuice base class.  Whilst in most cases this can 
   be as simple as ensuring that all these items in your application have a RoboGuice class as their base class, there are several situations where this is less than desirable. 
   One obvious case is where you are creating a reusable library that does not have any Activities, but simply exists as basic code libraries or perhaps pre-packaged views.
	
2. AndroidIOC does not depend on specific Android versions.  

   Other than the minimum Android API level requirement, AndroidIOC does not depend on specific implementations of Android classes.  Conversely because RoboGuice extends the default 
   behaviour of core Android classes (eg. Activity, ListActivity, MapActivity etc) whenever Google updates their core classes there is a risk that your application will cease 
   to function as expected in these newer versions.  This can also lead to problems supporting multiple API versions in your application.
   

.. include:: footer.inc	
