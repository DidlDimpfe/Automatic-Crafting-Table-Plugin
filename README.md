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

<img src="https://cdn.modrinth.com/data/SKieaOni/images/a2c1b0115c98178d570633002ef521a1fae02a91.png"  alt=""/>

<p>Now put items in this menu for any crafting recipe you want (custom recipes included). For instance, I use a block of iron:</p>

<img src="https://cdn.modrinth.com/data/SKieaOni/images/34f682a24e19c832b9b73688d521020d93611134.png"  alt=""/>

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
crafting-table-ui-space-display: 'space'
crafting-table-timer: 4
crafting-table-ui-need-permission: false
crafting-table-visual-feedback-enabled: true
crafting-table-sound-feedback-enabled: true</pre>

<p>The field "enabled" can be set either to true or false to enable or disable the plugin.</p>
<p>The field "crafting-table-ui-display" is the menu name of the menu that opens when you shift and left-click on a crafting table. Color codes supported.</p>
<p>The field "crafting-table-ui-space-display" is the name of the Black-Stained-Glass-Panes which serve as  a placeholder of the menu that opens when you shift and left-click on a crafting table. Color codes supported.</p>
<p>The field "crafting-table-timer" is a number of the time how long your crafting table needs to craft items. The number is in minecraft ticks. For instance: 20 minecraft ticks = 1 second.</p>
<p>The field "crafting-table-ui-need-permission" can be set either to true or false to make players need to have the "act.ui" permission to open the automatic crafting table ui.</p>
<p>The field "crafting-table-visual-feedback-enabled" enables/disables the particles that spawn when an item has been successfully automatically crafted.</p>
<p>The field "crafting-table-sound-feedback-enabled" enables/disables the sound that plays when an item has been successfully automatically crafted.</p>

<h2>PlaceholderAPI</h2>

<p>"automaticcraftingtableTables" returns an int of how many crafting tables are registered.</p>
<p>"automaticcraftingtableUsedRecipes" returns a String of all items that has been crafted automatically with the plugin.</p>