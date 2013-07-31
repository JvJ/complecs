# Complecs #

Welcome to Complecs.  Complecs is a real-time experiment in the design and implementation of a domain-specific language for the creation of 2D games, written in Clojure.  It is based on the [Simplecs](https://github.com/s-k/simplecs) library, and is powered by libGDX.

Clojure, being a dialect of Lisp, is particularly well-suited to iterative and interactive design and development.  So here's my crazy idea: I'll design as I code as I write this document.

Let's get started.

# Overview and Background #

Many of you who are interested in game programming, or perhaps programming in general, will be very used to object orientation.  In fact, many game programmers in particular will struggle to think of better ways to model a game world.  After all, the object analogy applies so well to in-game objects.

However, Clojure is not a particularly object-oriented language.  Despite the fact that it is built on top of Java and *can* interoperate with objects, it's designed to go beyond object orientation in many respects.  Additionally, it rejects many common idioms of object oriented languages.  It's dynamically-typed, functional, has a strong focus on immutable data structures, and good support for concurrent programming.

Perhaps you are now asking yourself: "Object orientation is great.  Why would I use such a ridiculous language to create games?".  While it's true that object orientation is a good way to make games, is it the *best* way to make games?  If you were designing a programming language from scratch for game creation, would it be object oriented?  Not necessarily.  There are other ways.

That brings us to the most important feature of Clojure: it is a Lisp.  That means that it is a programmable programming language; new control structures, code transforming operators, and even programming paradigms can be written as an extension of the language itself.  So when it comes to game development, we won't just be *using* a programming language, we'll be *making* one.

## The Component-Entity-System Model ##

Perhaps, in reading the last section, you thought something like "Something better than object orientation?  No way, jackeroonie!".  Well, here's where I introduce an important idea that will be the foundation of Complecs: the component-entity-system (CES) model.

CES is a way to model objects existing in a world *without* using object-orientation.  (Disclaimer: That doesn't mean object orientation *can't* be used to create a CES.  The Unity3D game engine is a very good example of such a case.)  In this document, I'll guide you through the creation and use of a CES, which will then be used in game creation.  Rather than relying on those paradigms envisioned by the creator of the Clojure language (Rich Hickey, peace be upon him), we will be introducing our own paradigm, and using it to create our application. Now, we're playing with power!

A CES models a program using three different things:
	- Components
	- Entities
	- Systems
	
Wow.  Big surprise, right?

Here's the skinny on how it works.  We have a collection of objects (entities).  Fundamentally, an entity is just a unique identifier (usually a number).  This would clearly be very boring if there wasn't something else to them.  Well, there is!  Each entity is associated with a collection of components, which contain its data.  For instance, a game object might have a `position` component, which contains its `[x y]` values.  Or it may have a `sprite` component, which contains references to the images that are drawn to animate the entity.  The important thing to keep in mind is that coponents represent *data*.

Clearly, whenever we have data in a computer program, we would like to do something with that data.  This is the purpose of systems.  Systems operate on components each update cycle, examining or changing them as necessary.  There could be a `gravity` system, which reduces the `y` value of the `position` component, or a `render` system, which draws the `sprite` to the screen.  Systems may be designed to run only on particular components, in which case they will execute once per component every update cycle, or they can be global systems, designed to run only once every update with no associated entity.

Where object orientation lumps together identity, data, and functionality into a single thing -- the *class* -- CES breaks these up into manageable chunks.  Each component has its own systems, and any arbitrary set of components may be grouped together in a given entity.  To translate this into object oriented terms, it's like there is no class.  Instead, one defines a set of data members, and a set of methods for those data members.  Then, to make an object, you just pick and choose which data members you want, throw them in a bag, and call it a day.

For our CES, we'll be using a project called Simplecs (which I didn't write).
