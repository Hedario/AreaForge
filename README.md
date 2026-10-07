# AreaForge
![SpigotMC](https://img.shields.io/badge/platform-Spigot%20%7C%20Paper-yellow?style=flat-square)
![Made with 💙](https://img.shields.io/badge/Made%20with-%F0%9F%92%99-blue?style=flat-square)

AreaForge is a modern and standalone area management and restoration plugin developed to consolidate the functionalities of [AreaReloader](https://modrinth.com/plugin/areareloader) and [AreaReloader-FAWE](https://modrinth.com/plugin/areareloader-fawe) into a single solution.

The plugin is designed with cross-platform compatibility in mind, supporting both Spigot and Paper while providing version-specific implementations when possible.

AreaForge utilises asynchronous implementations and separate threads to better distribute its workload, preventing excessive overheading on the main thread.

Generally speaking, the plugin will perform better on servers running Paper; moreover, for **<ins>Paper only</ins>** servers and **<ins>specific game versions</ins>** it's possible to enable NMS implementations to perform a more direct block placement, drastically reducing the overhead and significantly improving performance during large-scale area operations.
<details>
<summary>NMS versions</summary>
  
- 1.21.8
- 1.21.9
- 1.21.10

</details>

AreaForge has been compiled in Java 21 and officially tested in versions 1.20.5 – 1.21.11, the plugin should be compatible with any other version as long as the server running it uses Java 21 or similar.

## Configuration
<details>
<summary>Permissions</summary>

- areaforge.command.cancel
- areaforge.command.list
- areaforge.command.create
- areaforge.command.load
- areaforge.command.delete
- areaforge.command.help
- areaforge.command.version
- areaforge.command.reload
- areaforge.command.info
- areaforge.command.settings

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
<img src="https://bstats.org/signatures/bukkit/AreaForge.svg">
