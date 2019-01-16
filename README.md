![Kompass](https://github.com/sellmair/kompass/blob/develop/assets/Kompass_724.png?raw=true)

## A powerful router for Android, written in Kotlin
<br>

![GitHub top language](https://img.shields.io/github/languages/top/sellmair/kompass.svg)
[![Build Status](https://travis-ci.org/sellmair/kompass.svg?branch=develop)](https://travis-ci.org/sellmair/kompass)
![Bintray](https://img.shields.io/bintray/v/sellmair/sellmair/kompass.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/sellmair/kompass.svg)
[![Gitter](https://img.shields.io/gitter/room/nwjs/nw.js.svg)](https://gitter.im/kompass-android/)


#### Support
I am happy to help you with any problem on [gitter](https://gitter.im/kompass-android/Help), as fast as I can! <br>
Alternatively, just open a new issue! 

# Why you should use Kompass
- Powerful router which works great with MVP, MVVM and any other architecture
- Boilerplate free routing: No more bundle.putInt(ARG_SOMETHING, something)
- Very simple and clean architecture for applying custom transitions
- Generic fragment transitions
- Routing with multiple screen (which are called ships)
- Kotlin

# Setup
##### Step 1: Enable Annotation Processing
Add this at the top of your build.gradle
```groovy
apply plugin: 'kotlin-kapt'


kapt {
    generateStubs = true
}

```

##### Step2: Add Kompass Dependencies
```groovy
dependencies {
    ...
    implementation 'io.sellmair:kompass:0.1.0-beta.0'
    implementation 'io.sellmair:kompass-annotation:0.1.0-beta.0'
    kapt 'io.sellmair:kompass-processor:0.1.0-beta.0'
}
```


# Usage
## Example
I highly recommend having a look at the [example](https://github.com/sellmair/kompass/tree/develop/example) app built with Kompass

<br><br>
###### Gif
<img src="https://github.com/sellmair/kompass/blob/develop/assets/example.gif?raw=true" width="250">
<br><br>


## Basic
Kompass uses a ship related naming schema. Here are the types that you will encounter

- 🏰 ```Kompass```  is the upper most object and contains multiple ships

- 🛶️ ```KompassShip```  is the entity which can route to a certain _Destination_. This might represent a certain area of your 
activity where fragments can be loaded: e.g. One Ship can route to views/fragments on the top of the screen
while another Ship is able`to display content on the bottom of the screen. You can have as many ships as you want
in your App.

- ⛵ ```KompassSail```️ is the actual area where fragments can be placed in. Your activity therefore sets the 
sails for a certain ship, which then 'sails' to the destination.
 You might want to use ```FrameLayout``` most often as target for your fragments
 
- 🏖  ```Destination``` represents one certain 'scene' of your app. It also holds all necessary arguments for 
 the fragment/activity. For example: You might have a 
'LoginDestination', 'HomeDestination', 'SettingsDestination', ...  in your application. 
You can use plain kotlin (data) classes to represent destinations

- 🗺 ```KompassMap```  knows how to display a certain _Destination_ (meaning which Fragment/View/Activity to load for it). 
A map (_AutoMap_) is automatically created for you

- 🏗 ```KompassCrane```   knows how to push a _Destination_ object into a _Bundle_. A Cran (_AutoCran_) is automatically
created for you

- 🎢 ```Detour```  can implement custom transitions/animations for certain routes. 
Just implement a ```KompassFragmentDetour``` or ```KompassViewDetour``` 


#### Create a Kompass
Creating the _Kompass_ is very simple using the provided builder: 

###### Create a Kompass: Trivial
This example is the most trivial Kompass that can be built. It accepts any object implementing
_KompassDestination_ as Destination. We will talk about the .autoMap() part later. 
It is easy, I promise :bowtie:
```kotlin
val trivialKompass = Kompass.builder<KompassDestination>()
                     .autoMap() // we will talk about this later
                     .build()
```

___
<br><br>
###### Create a Kompass: Real World Example
Here is a real-world example of Kompass, where _MyCustomDestinationType_ is just a basic
sealed class and 'autoMap', 'autoCrane' and 'autoPilot' are extension functions automatically 
generated by the _KompassCompiler_. But as you can see: It is very easy to create a Kompass object :blush:

```kotlin
val kompass = Kompass.builder<MyCustomDestinationType>()
                     .autoMap()
                     .autoCrane()
                     .autoDetour()
                     .build()
```


#### Create your _Destinations_
Destinations are simple classes or data classes which hold very simple data like 
- ```Float```
- ```Int```
- ```String```
- ```List<Float>```
- ```List<Int>```
- ```List<String>```
- ```FloatArray```
- ```IntArray```
- ```Array<String>```
- ```Parcelable```
- ...

(Everything that can be represented inside android.os.Bundle plus some additions)

Destinations are typically annotated with 
```kotlin
@Destination(target = [MyFragmentOrActivity::class])
```
 
 I personally consider it a good idea implementing a sealed superclass for groups of _Destinations_ and restrict 
 the Kompass object to this superclass. 
 
 ###### Example: Annotated Destination
 ```kotlin
@Destination
class HomeDestination(val user: User)
```

 
#### Set sails to a Ship
Once your activity has started, you have to provide a sail for the ship which should route to certain 
destinations. You can do this whenever you want to (even after you routed your ship to a certain
destination). The following example will show how the FrameLayout with id 'R.id.lisa_container' will 
be used for the ship called Lisa as _Sail_: 

```kotlin
class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Step 1: Get the kompass instance (Dagger, Kodein, Singleton?, ...)
        val kompass = ...
        
        // Step 2: Get the ship called 'lisa'
        val lisa = kompass["lisa"]
        
       
    }

    override fun onStart(){
        super.onStart()
        // Step 3: Set the sail and release it automatically by lifecycle
        lisa.setSail(sail(R.id.lisa_container)).releasedBy(this)
    }
}
```

#### Route to a Destination
Now it is time to route to a certain destination. The following example will show how the routing
for a login-screen could look like: 
Side-note: ```kompass.main``` is a little convenience extension for ```kompass["main"]```

```kotlin
    
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = getUser()

        sail = kompass.main.setSail(this, container.id)
        kompass.main.navigateTo(if(user!=null) HomeDestination(user) else LoginDestination())
        
    }
```


#### Recreate Destination from _Bundle_
One of the strongest parts of _Kompass_ is the elimination of hassle with bundles and arguments. 
You can easily recreate the original _Destination_ from an intent or bundle using the automatically
generated extension functions. 

##### Example: Fragment
If you routed to a certain fragment you can easily recreate the destination from the arguments _Bundle_
```kotlin
class HomeFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val homeDestination = arguments.asHomeDestination() // Function was automatically generated
        val user = homeDestination.user
        // ... Do something with your user object
    }
}
```

##### Example: Activity
```kotlin
class HomeActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        val homeDestination = intent.extras.asHomeDestination()
        val user = homeDestination?.user
        // ... Do something with your user object
    }
}
```

## Advanced
#### 🗺 The Map 
Maps contain information about how to display a certain _Destination_. This can be done by 
starting a new Activity or creating a new Fragment. If you want to use a custom Map element, add it to the KompassBuilder
```kotlin
Kompass.builder()
       .addMap(myCustomMap)
       ...
```
#### 🏗 The Crane 
A crane knows how to pack a _Destination_ object into a bundle. If you want to use a custom Cran, 
add it to the KompassBuilder 

```kotlin
Kompass.builder()
       .addCrane(myCutomCrane)
```
#### 🎢 The Detour 
It is a very common thing to apply transitions when one fragment is replaced by another fragment. 
A _Detour_ can very easily implement such a transition generically. 

Consider we want every fragment to slide in, when entered and slide out, when exited. We just 
have to write a _Detour_ class like this: 

```kotlin
    @Detour
    class FragmentSlide: KompassFragmentDetour<Any, Fragment, Fragment>{
        override fun setup(destination: Any,
                           currentFragment: Fragment,
                           nextFragment: Fragment,
                           transaction: FragmentTransaction) {
            currentFragment.exitTransition = Slide(Gravity.RIGHT)
            nextFragment.enterTransition = Slide(Gravity.LEFT)
        }

    }
```

Every _Detour_ will automatically be applied if the types of 'destination', 'currentFragment' and 'nextFragment' 
can be assigned from the current route and 

```kotlin
Kompass.builder()
       .autoDetour() // <-- will be available if you have some class annotated with @Detour
```
is used!

#### AutoMap, AutoCrane, AutoPilot
The functions 
```kotlin
Kompass.builder()
       .autoMap()
       .autoCran()
       .autoDetour()
```
are automatically generated if possible. 

- .autoMap() will be available after you specified one target for at least one @Destination
- .autoCrane() will be available after you annotated at least one class with @Destination
- .autoDetour() will be available after you annotated at least one class with @Detour

#### BackStack
Kompass comes with an own back-stack. You should override your Activities 'onBackPressed' like: 

```kotlin
    override fun onBackPressed() {
        if (!kompass.backImmediate())
            finish()
    }
```

You can add custom elements to the back-stack by passing a lambda to ```Kompass```

```kotlin
kompass.onBack {
  // do something
}
```
