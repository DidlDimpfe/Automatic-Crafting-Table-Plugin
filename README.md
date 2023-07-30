<h1>Automatic-Crafting-Table</h1>

<img src="https://cdn.modrinth.com/data/SKieaOni/images/b5e4a22745cfc967d003e108efd4128cc8b9c37a.png" width="500" height="500" alt=""/>

<p>You can now automatically craft items within a hopper system. </p>
<p>This plugin supports: </p>

<ul>
<li>All minecraft versions from 1.14.1 to 1.19.4</li>
<li>Integration of a workbench in a hopper system to automatically craft items</li>
<li>Custom items</li>
<li>PlaceholderAPI</li>
<li>A configuration file</li>
</ul>


<h2>How to use it</h2>
<p>Simply shift and left-click on a crafting table and a new menu pops up:</p>

<img src="https://cdn.modrinth.com/data/SKieaOni/images/e89da135773a87b520d3788368632e4074bd22f9.png" alt=""/>

<p>Now put items in this menu for any crafting recipe you want (custom recipes included). For instance, I use a block of iron:</p>

<img src="https://cdn.modrinth.com/data/SKieaOni/images/d4de8f868097d2ca8df78b23efedda7ae56531c3.png"  alt=""/>

<p>In the next step, connect your crafting table you just modified with a hopper system (the first hopper must go on the crafting table, the second hopper must go in any other direction except the where the crafting table is located). For example:</p>

<img src="https://cdn.modrinth.com/data/SKieaOni/images/394c407f1975001fc0e5984e593bca0f1a76f8bd.png"  alt=""/>


<p>Finally, just put the items you need to craft the recipe in the hopper that goes into the crafting table and Voilà!
The item you want just dropped into the other hopper!</p>

<p>You can also make more than one hopper face into the crafting table. They will work 
together with the ingredients to craft the item you want. For example, one hopper has 2 sticks
and the other has 3 diamonds for a diamond pickaxe and it would work!</p>

<h2>The configuration file </h2>
<pre>
enabled: true
crafting-table-ui-display: 'Automatic Crafting Table Recipe'
crafting-table-timer: 4
crafting-table-visual-feedback-enabled: true
crafting-table-sound-feedback-enabled: true
separate-from-other-crafting-tables: false
automatic-crafting-table-item-display: "&aAutomatic-Crafting-Table"
automatic-crafting-table-item-lore:
  - "&eUse this Crafting Table in a "
  - "&ehopper system to automatically "
  - "&ecraft items"</pre>

<p>The field "enabled" can be set either to true or false to enable or disable the plugin.</p>
<p>The field "crafting-table-ui-display" is the menu name of the menu that opens when you shift and left-click on a crafting table. Color codes supported.</p>
<p>The field "crafting-table-timer" is a number of the time how long your crafting table needs to craft items. The number is in minecraft ticks. For instance: 20 minecraft ticks = 1 second.</p>
<p>The field "crafting-table-visual-feedback-enabled" enables/disables the particles that spawn when an item has been successfully automatically crafted.</p>
<p>The field "crafting-table-sound-feedback-enabled" enables/disables the sound that plays when an item has been successfully automatically crafted.</p>
<p>The field "separate-from-other-crafting-tables" makes, when it is set to true, the Automatic-Crafting-Table a different item as the normal crafting table. Do /getACT (permission: act) to get one.</p>
<p>The field "automatic-crafting-table-item-display" is the display of the Automatic-Crafting-Table-Item.</p>
<p>The field "automatic-crafting-table-item-lore" is the lore of the Automatic-Crafting-Table-Item.</p>

<p>You can also make more than one hopper face into the crafting table. They will work together with the ingredients to craft the item you want. For example, one hopper has 2 sticks and the other has 3 diamonds for a diamond pickaxe and it would work!</p>

<h2>PlaceholderAPI</h2>

<p>"automaticcraftingtableTables" returns an int of how many crafting tables are registered.</p>
<p>"automaticcraftingtableUsedRecipes" returns a String of all items that has been crafted automatically with the plugin.</p>