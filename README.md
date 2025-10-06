<img src="https://i.imgur.com/gBVSU6U.png" alt = "Logo">

[![Athena Award Badge](https://img.shields.io/endpoint?url=https%3A%2F%2Faward.athena.hackclub.com%2Fapi%2Fbadge)](https://award.athena.hackclub.com?utm_source=readme)

# NTM Immersive Portals
**....is a Minecraft mod that makes the TARDIS in NTM use Immersive Portals for a bigger on the inside effect with seamless transitions!**

***This mod may be used in any modpack.***

**This mod needs to be installed on _BOTH_ the _SERVER_ and the _CLIENT_!**

<img src="https://i.imgur.com/IW7tznS.gif" alt = "Short video example of mod">

### ➕ Features

- Seamless portals for the TARDIS :D
- Allows for compatibility with addons that create custom exteriors

### 👏 Credits
- Thanks to Lilith and the rest of the dev team for making New TARDIS Mod!
- Thanks to the team at Immersive Portals for making Forge support a thing!

### ❔ Motivation
If you didn't see already at the top, this mod was created for the Athena Awards! Without a further do, lemme yap about some things relating to this mod's creation.

- Why make this mod?

So a while back, I created a mod identical to this for an older version of the TARDIS mod for me and my friends to play with personally. I thought it was an absolutely amazing feature because to me, it is just so awesome to be able to go between dimensions SEAMLESSLY without having to watch a boring loading screen. Hence, when New TARDIS Mod and Immersive Portals both released for 1.20.1, I took the opportunity to implement Immersive Portals into NTM for not only myself, but for people I know in the mod community who would love to use this mod as well!

- How was it was created?

Using software such as Intellij IDEA, and tools like mixins, I was able to modify the methods within both mods and implement my own code in order to get the two to work together seamlessly. With that said, I also had to create individual portals for each TARDIS exterior to make sure the portals lined up perfectly. As a bonus, in the event that users were to add their own custom exteriors into NTM, the mod has a custom forge registry that allows for these addons to add their own portal sizes into my mod, allowing for complete compatibility!

-  How did I struggle and what did I learn?

I'll have to be honest, making this mod was tough! One of the major issues I had while developing the mod was dealing with Immersive Portals. Immersive Portals was a pain in the ass to get working 100%. Sometimes the mod would crash because it could not handle the dimension code that NTM used to create their dimensions. I often ran into packet issues which would persist even when I was almost complete with the mod. By then, I knew I had to get the bug fixed, otherwise it'd be a glaring issue that many would bring up to me. So.. I went ahead and fixed it.. by patching it in the **worst** way possible 😅. What I did is I ended up overwriting methods that were crashing in the mod to get them to work with NTM. This of course.. is not good practice as a modder because it's not like the modding community revolves around your mod! Though, at the moment, I cannot think of any other way to fix it, and.. well.. as a programmer once said.. "if it ain't broke, don't fix it!" On the flip side, I realized it was possible for users to develop their own exteriors while simultaneously adding compatibility with this mod, so I went ahead and coded the ability for that to happen into the mod!

Thank you Athena Awards for presenting me with this opportunity to make cool shit and get cool shit out of it :)
