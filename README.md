# AreaForge
![SpigotMC](https://img.shields.io/badge/platform-Spigot%20%7C%20Paper-yellow?style=flat-square)
![Made with 💙](https://img.shields.io/badge/Made%20with-%F0%9F%92%99-blue?style=flat-square)
AreaForge is a modern and powerful standalone area management and restoration plugin, developed to save areas within a selection at the stage of their creation, to then restore them at a later moment, either manually or automatically.

AreaForge is born to consolidate the features and functionalities of [AreaReloader](https://modrinth.com/plugin/areareloader) and [AreaReloader-FAWE](https://modrinth.com/plugin/areareloader-fawe) into a single solution.
<img src="https://cdn.modrinth.com/data/cached_images/ee799f29b31958ba41b8ccc39b05b16dba480580.png" alt="af_background">



The plugin is designed with cross-platform compatibility in mind, supporting both Spigot and Paper while providing version-specific implementations when possible.

AreaForge utilises asynchronous implementations and separate threads to better distribute its workload, preventing excessive overheading on the main thread.

Generally speaking, the plugin will perform better on servers running Paper; moreover, for **<ins>Paper only</ins>** servers and **<ins>specific game versions</ins>** it's possible to enable NMS implementations to perform a more direct block placement, drastically reducing the overhead and significantly improving performance during large-scale area operations.
<details>
<summary>NMS versions</summary>
  
- 1.21.8
- 1.21.9
- 1.21.10

</details>

## Key features
### Block management
  
AreaForge allows area management by creating a copy of a selected area, restorable at any moment both manually and automatically, all at the ease of a wooden axe and a command!

### Containers and entities management
  
AreaForge doesn't stop at simple blocks restoration but allows to save and later load both entities and containers.

### Player teleportation
After setting a safe location through /af settings <area> location set, all players standing in the loading area will be teleported to the set safe location.

### PlaceHolderAPI compatibility
  
AreaForge supports PlaceHolderAPI to display various information about areas:

### SQLite & MySQL system
Information about areas is saved to database; by default SQLite will be enabled as the engine to use but it's also possible to select MySQL by setting it up in the config.yml

### Interactive text
Most of the plugin's texts are interactable by left clicking, providing shortcuts to commands, settings and links!
Interactable texts from commands: **Help, Settings, Version, Info.**
<details>
<summary>Example</summary>
  
<img src="https://cdn.modrinth.com/data/cached_images/cb062af11eaa36b210845707d9aceb92a03efa6d.jpeg" alt="af_background">

</details>

The plugin finds its largest use in the restoration of pvp and pve arenas, as well as restoring limited event chests containing tresures.

## Compatibility
AreaForge has been compiled in Java 21 and officially tested in versions 1.20.5 – 1.21.11, the plugin should be compatible with any other version as long as the server running it uses Java 21 or similar.

## Configuration

<details>
<summary>Config file</summary>

The configuration file is really is to setup and primarly contains language settings.<br>
Important fields:
<table>
  <tr>
    <th>Category</th>
    <th>Field</th>
    <th>Default</th>
    <th>Values</th>
    <th>Description</th>
  </tr>
  <tr>
     <td>Metrics</td>
     <td>Enabled</td>
     <td>true</td>
     <td>true/false</td>
     <td>Enables metrics from bStats to gather data about the plugin's usage.</td>
  </tr>
  <tr>
     <td>Storage</td>
     <td>Engine</td>
     <td>SQLITE</td>
     <td>SQLITE/MYSQL</td>
     <td>Sets the engine to use for the database connection.</td>
  </tr>
  <tr>
     <td>Budget</td>
     <td>Interval</td>
     <td>1</td>
     <td>Any number > 0</td>
     <td>Sets the interval between area loading ticks.</td>
  </tr>
  <tr>
     <td>Budget</td>
     <td>Time</td>
     <td>0.02</td>
     <td>Any number > 0</td>
     <td>Sets the maximum time dedicated to AreaForge for area creations and loadings.</td>
  </tr>
  <tr>
     <td>Loading</td>
     <td>Apply physics</td>
     <td>false</td>
     <td>true/false</td>
     <td>Decides whether or not apply physics to blocks when loading areas.<br>Keeping this diabled may improve performance in certain situations.</td>
  </tr>
  <tr>
     <td>NMS</td>
     <td>Enabled</td>
     <td>true</td>
     <td>true/false</td>
     <td>Whether or not to use NMS implementations.<br>
       NMS implementations can drastically improve the loading speed of areas by using interal structures.<br>
       While it can benefit server's performance it may create entity and/or light glitches in certain scenarios.</td>
  </tr>
</table>

</details>


<details>
<summary>Commands</summary>

- /af help [page, topic]
A generic help command, provides a list of available commands and description when one is specified.<br>
- /af create <area> [save containers] [save entities]<br>
Allows for the creation of an area within the created selection.<br>
**save containers** whether or not inventories should be saved upon the creation of the area.<br>
**save entities** whether or not entities should be saved upon the creation of the area.<br>
- /af reload
Reloads the plugin's configurations and mechanisms.<br>
- /af cancel <area>
Cancels the loading of one or all areas.<br>
- /af settings <area> <auto_load, auto_time, location> <true/false, time, set>
Allows to manage an area's settings to manage auto loading and safe location.<br>
- /af load <area>
Allows the loading of the specified area.<br>
- /af list [page]
Provides a list of all created areas.<br>
- /af delete <area>
Deletes an existing area from the database and the folder.<br>
- /af info <area>
Shows information about an existing area.<br>
- /af version
Shows the current plugin's version and provides useful links.<br>

</details>

<details>
<summary>Permissions</summary>
  
- areaforge.command.admin<br>
Gives access to all permissions below
- areaforge.command.cancel<br>
Gives access to the /af cancel command
- areaforge.command.list<br>
Gives access to the /af list command
- areaforge.command.create<br>
Gives access to the /af create command
- areaforge.command.load<br>
Gives access to the /af load command
- areaforge.command.delete<br>
Gives access to the /af delete command
- areaforge.command.help<br>
Gives access to the /af help command
- areaforge.command.version<br>
Gives access to the /af version command
- areaforge.command.reload<br>
Gives access to the /af reload command
- areaforge.command.info<br>
Gives access to the /af info command
- areaforge.command.settings<br>
Gives access to the /af settings command

</details>

## Issues
Open a new issue [here](https://github.com/Hedario/AreaForge/issues).
<br>
When opening a new issue please add and specify:

- Provider's version (spigot, paper, bukkit) - /version;
- AreaForge's version - /af version;
- Nature of the issue: command, auto reloading, loading, creation, settings, etc.
- Error trace of the issue (logged to console).

## Support

<p align="center">
    <a href="https://discord.com/invite/yqs9UJs">
        <img src="https://i.imgur.com/JgDt1Fl.png" width="300" alt="discord">
    </a>
    <br>
    <i>I do my best to provide support for my projects over discord.
      <br>If you'd have questions or support requests feel free to join!</i>
</p>

## Metrics
This plugin collects anonymous server statistics which helps me keep track of the plugin's usage.<br>
I invite you to keep this setting on as it contributes to boosting my dedication and work towards my projects!
Provided by [bStats](https://bstats.org/).
<img src="https://bstats.org/signatures/bukkit/AreaForge.svg" alt="bStats">
