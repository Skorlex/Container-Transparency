# Container Transparency

**Container Transparency** gives you complete control over the opacity and visual clarity of your inventory screens (Crafting Table, Enchanting Table, Anvil, etc.)

### Key Features

* **Container Base & Slot Customization**: Adjust the background opacity of all containers screen (chests, barrels, shulker boxes, furnaces, crafters, villagers, etc.) while keeping slot highlights independently adjustable.
  
* **Text Brightness (0%–200%)**: Change inventory titles using linear HSB color scaling. Shift text toward crisp white or deep black to maintain contrast against see-through menus or dark-mode packs.
  
* **Separated Potion Controls**: Adjust potion effect displays in your inventory versus the active HUD on your main screen (separate controls for icons, background badges, and duration timers).
  
* **Recipe Book**: Adjust recipe slot opacity and the book toggle icon separately, with the search bar and filter button syncing directly to your base container setting.
  
* **High Resource Pack Compatibility**: Built with an extensive container keyword engine to ensure seamless transparency across GUI packs like *Recolorful Containers* and *Colourful Containers*.
  
* **Keybind**: Press **F10** (configurable under Controls > Key Binds) to open the config screen.

### ⚠️ Compatibility Warning

* **Custom Modded Containers**: Menus added by third-party mods that do not extend vanilla's `AbstractContainerScreen` or that render using custom rendering pipelines will not be affected by transparency.
  
* **Other GUI Overhauls**: Running alongside mods that completely rewrite container rendering or conflict with `GuiGraphicsExtractor` hooks may cause visual glitches.
  
* **Non-Standard Resource Packs**: While most GUI packs (such as *Recolorful Containers* and *Colourful Containers*) are supported, packs using completely arbitrary texture paths without standard container keywords will remain opaque.
