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
