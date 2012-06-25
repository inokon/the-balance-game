the-balance-game
================

Current revision: 0.5 ALPHA

Author: Simone Autore (aka Sippolo)

The Balance Game is a Desktop/Android game born with the idea of balancing in mind, with a good variety of stages decently difficult to win, a very simple gameplay and nice graphics.
The game makes use of the most awesome cross-platform Java game/graphics development framework: LibGDX Framework: http://libgdx.badlogicgames.com/
It also makes use of the great Box2dLights library from kalle.h: http://code.google.com/p/box2dlights/


ALPHA STAGE NOTE: please note that this game is still in alpha stage, and unfortunately the development has halted some time ago, so I decided to make it open source for you for learning purposes or even if you decide to fork it and complete it. You're free to do anything with this source, just follow the license here included (Apache License 2.0), though I'd really appreciate if you let me know whenever you'd be doing cool stuff with this project!


- IDE Integration:

You should be able to import the project directly into Eclipse and compile it without issues.


- Run instructions:

Android: just copy the apk file from the android version folder to your android device and install as with any other apk file (granted it won't need strange permissions!)

Desktop: make sure you have at least Java jre6 or newer version, and just run the jar file inside the desktop version folder.


- Gameplay and Commands

The game makes use of the device accelerometer to move the objects (on desktop the mouse will do the trick), and also finger taps (or mouse click on desktop) in certain levels.
Here is a description of actual implemented levels:

Level 1 (Difficulty: Hard): 
	You must last 60 seconds to win!
	Keep the red sphere in balance, don't let it fall off the big bar, and simultaneously catch all the little blue spheres
	with the little bar, don't let them fall off too!
	For each lost little blue sphere you lose a life, and you only have 3!
	If you lose the red sphere, it's gameover!
	
Livello 2 (Difficulty: Hard):
	Exactly the same as Level 1, plus now you'll have to deal with tricky portals!
	
Livello 3 (Difficulty: Hard):
	You must last 60 seconds to win!
	Keep the violet boxes in balance, don't let them fall, and simultaneously catch all the little blue spheres
	with the little bar, don't let them fall off the screen!
	For each lost little blue sphere you lose a life, and you only have 3!
	If you lose even one violet box, it's gameover!
	
Livello 4 (Difficulty: Medium):
	You must last 60 seconds to win!
	Move the basket and catch all the green spheres, while keeping it away from bombs!
	For each lost green sphere you lose a life, and you only have 3!
	If you take just one bomb, well... Explosion!
	
Livello 5 (Difficulty: Medium):
	You must last 60 seconds to win!
	Move the colored spheres that fall from above, and put them in the matching tube (look at the colors!)
	For each lost sphere you lose a life, and you only have 3!

Livello 6 (Difficulty: Hard):
	You must last 60 seconds to win!
	Keep the red sphere in balance on that column, don't let it fall!
	Use single tap to jump, and another single tap while jumping to perform a double jump.
	Holded tap for about half second will make the sphere shrink, and will return normal after some instants.
	Finally...avoid the flaming lasers!
	
Livello 7 (Difficulty: Medium):
	You have ONLY 60 seconds to clear the level!
	Drag and drop the boxes from the platform on top-left of the screen, to the platform where big spheres are placed.
	Be quick and build any vertical construction to reach the red line above the screen before the time ends!
	Don't let boxes fall off the screen or you'll lose lives!
	And as soon as you lose one sphere, it's gameover!
	
Livello 8 (Difficulty: Medium):
	You must last 60 seconds to win!
	This is a remastered version of the famouse Arkanoid game.
	You have 3 balls to battle the incoming brick walls, but don't you dare lose them all, otherwise it's gameover!
	Also, don't let the brick wall fall below the little bar, otherwise it's gameover again!
	
Livello 9 (Difficulty: Medium):
	You must last 60 seconds to win!
	Just avoid the obstacles on your way, by using single tap to jump and another tap while jumping to perform a double jump!
	If if fall off the screen, it's gameover!
	
Livello 10 (Difficulty: Hard   NOTE: Graphics not implemented so you'll hardly see which is a black hole and which is a white hole in this stage):
	You have ONLY 60 seconds to clear the level!
	Move your device to let the ball move across the screen, and try to reach the top of it before the time ends!
	Stay away from the black holes that will try to attract you, if they catch you, you're dead.
	The white holes will try to repulse you instead, so be careful.
	You'll have a big surprise just before reaching the top!