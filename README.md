# BusinessMonkey

## Game Design Document

### Game Summary

Abu never wanted to go apeshit. But when his business associate ran away with his jewels, he knew what he had to do. Your mission: Shoot your way through the rival triad group and track down the boss to get your jewels back!

Fight your way through unique levels by shooting down waves of enemies and advancing toward their boss in this fast-paced first-person shooter. Utilize a variety of guns, temporary power-ups, and parkour-style manic-monkey movements to quickly take down enemies and maneuver the streets and buildings of Hong Kong.

### Genre

This game is a first-person shooter with arcade-like elements such as powerups/pickups, short levels, and a scoring system. Power-ups provide temporary advantages (likely stat buffs like increased damage or speed), while pickups provide new weapons (from pistols to machine guns to banana rocket launchers). Levels are designed to permit some level of exploration between fights but also promote short and intense encounters with large waves of enemies. The scoring system would reward skill, with higher scores for faster times or more difficult shots, encouraging players to play repeatedly and achieve higher scores. This will also let them experiment with parkour, to more efficiently traverse the map upon different attempts.

### Inspiration

Primary sources of inspiration were Doom and Superhot. The fast-paced first-person shooting of Doom is the perfect mood for this maniacal shooter. Additionally, its arcadey aspects with item pickups and short, linear, wave-based levels. Superhot has a nice low-poly art style which is a solid art style that is relatively quick to prototype games with.

![Doom Image](https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fimages.pushsquare.com%2Fscreenshots%2F103600%2Flarge.jpg&f=1&nofb=1&ipt=48558be15b865f387e14a68127de6a1579900413b41cbcfef43a896ebf8de1eb&ipo=images)

![Superhot image](https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.cgmagonline.com%2Fwp-content%2Fuploads%2F2017%2F07%2Fsuperhot-ps4-review-super-cool-9.jpg&f=1&nofb=1&ipt=52abd44752b9b5562a277215f79e570d3a5968e052ac973daf33660b391f58f2&ipo=images)

Additionally, we like the parkour/movement of Neon White, Titanfall 2, and Mirror's Edge. We are especially excited to add the wall running, double jumping, and environmental boosts (i.e. bounce pads) to our game if we have time.

![Titanfall 2 Image](https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fstatic0.gamerantimages.com%2Fwordpress%2Fwp-content%2Fuploads%2Ftitanfall-pilot-wall-running.jpg&f=1&nofb=1&ipt=d6687f8505034fd2638038fada51e0d938a14e548dbf445e433f6840208bd2f1&ipo=images)

<https://en.wikipedia.org/wiki/Doom_Eternal>

<https://en.wikipedia.org/wiki/Superhot>

<https://en.wikipedia.org/wiki/Neon_White>

<https://en.wikipedia.org/wiki/Titanfall_2>

<https://en.wikipedia.org/wiki/Mirror%27s_Edge>

### Gameplay

The player is first-person controlled. The movement is fast-paced and fluid, where the player can run around and jump quickly while maintaining shooting accuracy. The maps will support the player's fast movement to encourage jumping between platforms and buildings while avoiding enemies in the dense environment of Hong Kong. Most enemies will be easy to kill, with the difficulty coming in quantity earlier in a level. However, as players progress, they will encounter more challenging foes and final bosses with custom attacks and levels that require strategic thinking and enhanced skills to beat. The HUD should be simple, taking up minimal space and only showing the necessary information, drawing inspiration from Doom, where controls would follow most PC FPS's and utilize WASD for movement and mouse for aiming and shooting.

Some ideas we would like to implement if we have time are...

- Multi-layered levels. i.e. in the streets of hong kong, you can run along the streets, above the awnings.
- Helicopter enemy. You just gotta dodge the bullets when the helicopter comes into the level, then maybe it goes away on its own pretty quick.
- Monkeys jumping out of the windows of the skyscrapers in the level, ambushing the player. Maybe as an intro sequence.

![Hong Kong multi-layered streets](https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwallpaperaccess.com%2Ffull%2F1922270.jpg&f=1&nofb=1&ipt=b4c3e5ad7778b1a9cba2b6a15b5266bb2b26feed20a21df00a1a68fa081b9d42&ipo=images)

### Final Results/Process

For the demo, we may keep the level as a rough block-out to focus on the gameplay mechanics. Additionally, enemy AI will be kept simple as it often becomes a large task that distracts from the player's mechanics (i.e. movement, shooting, etc.), which is more necessary for a fun game- especially a demo. This Ai may simply include stationary AI that shoots at the player, and/or basic walking towards the player.

## Development

### First Deliverable

We have coded a basic block-up of a small city level containing buildings, a street, and cars. These aspects are grouped together based on their type. i.e. all buildings are within a "buildings" sub-node, similarly for "ground" (containing the street and two sidewalks), and also the "cars" and "coins." The meshes are all simple, built-in meshes - boxes for the buildings, cars and ground; cylinders are used for the coins.

For interactions, the player can left click to shoot from the center of their screen. If shooting at a vehicle, the vehicle will react by taking damage (subtracting a value from it's individually stored health value) and rotating randomly a little to sell the impact (though no physics was implemented yet). Once the vehicle takes enough damage, it gets destroyed, revealing the coin within it.

The coins can be collected by pressing the spacebar while looking at it. It adds it's worth to the player's coinsCollected variable and outputs to the console the amount collected so far. In doing so, it also gets destroyed.

Screenshots unavailable due to progress! See next set of deliverables for screenshots of the later versions.

### Second Deliverable

We have implemented importing models, including buildings, monkeys, and cars into the game. To do this, we have properly utilized the assetmanager object. Additionally, the monkey models (seen in the second image below) is animated. Though, the car model's mesh is currently not displaying 100% correctly. Textures are also utilized to correctly render the color of the monkeys.

We have utilized the gui node to produce a health bar on the top left corner of the screen which currently goes down whenever the user presses the spacebar. When the player's health reaches 0, the screen tints red (also utilizing the gui node), and their movement is disabled.

Lighting has been implemented, including ambient, point, directional (sun), and spotlights. These properly illuminate the environment and shaded materials.

Finally, physics has also been implemented. The player may move forward and backward with W and S, as well as jump with spacebar. They can also look left and right with A and D.

![image](image.png)

![monkeys](image-1.png)

![dead](image-2.png)

### Third Deliverable

For the third deliverable, we introduced several enhancements to the game, focusing on both aesthetic and gameplay elements.

Fire Effect with Particles and Hurtbox: 
Using assets from the jMonkey Beginner’s Guide, we added a fire particle effect to certain areas of the game. This effect includes realistic flames and flickering, and we implemented a "hurtbox" so that when the player enters the fire area, their health decreases periodically. This mechanic uses a timer to inflict damage every second.
<img width="832" alt="Screenshot 2024-12-03 at 10 37 36 PM" src="https://github.com/user-attachments/assets/b6c8d222-d2db-402f-b545-66d64fc6e3ac">

Skybox: 
We added a skybox generated from a night sky using an online 3D Space Skybox Generator. This creates an immersive environment always present around the player.
<img width="791" alt="Screenshot 2024-12-03 at 10 37 43 PM" src="https://github.com/user-attachments/assets/a6633523-419a-4158-b896-c64776a05608">

Fog Effect: 
We also added a fog system to add atmosphere in confined spaces. The effect dynamically activates when the player enters certain close-cornered areas and is disabled otherwise.
<img width="1104" alt="Screenshot 2024-12-03 at 10 37 55 PM" src="https://github.com/user-attachments/assets/44470434-0e04-46db-bd9b-f0c87e8ef8f2">

Ambient Occlusion and Shadows have buggy implementations at this point, and bloom filter we couldn't get to in time.

## Sound
Though we did not get around to fully fleshing our game out with sound, we incorporated some aspects by adding ambient sound and by giving a collect sound when the player picks up a coin, and will implement sounds for player movement, taking damage, and fire burning.

## Member Contributions

Liam\
Designed and coded the fire particle effect and hurtbox mechanic.
Implemented the skybox and fog systems.
README

Trevor\
Working on Enemy Monkeys
Interactions between Coins and Cars
BLoom Filter

Sam\
Sound work
Game State Menu

Collaborative Contributions\
All members worked on level design, acquiring the resources for sounds, sprites, generating ideas for what to implement, and debugging the code.

Acknowledgment\
We utilized assets and guidance from the jMonkey Beginner’s Guide, 3D Space Skybox Generator, and other online sources. Links to specific tools and resources are provided to credit these inspirations.
