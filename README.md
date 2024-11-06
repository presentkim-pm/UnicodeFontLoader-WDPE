<!-- PROJECT BADGES -->
<div align="center">

![Version][version-badge]
[![Stars][stars-badge]][stars-url]
[![License][license-badge]][license-url]

</div>


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <img src="https://raw.githubusercontent.com/presentkim-pm/UnicodeFontLoader/main/assets/icon.png" alt="Logo" width="80" height="80">
  <h3>UnicodeFontLoader</h3>
  <p align="center">
    An waterdog plugin that automatically load unicode font images and pack them into resourcepack!

[Contact to me][author-discord] · [Report a bug][issues-url] · [Request a feature][issues-url]

  </p>
</div>


<!-- ABOUT THE PROJECT -->

## About The Project

> THIS PLUGIN IS PORT OF [UnicodeFontLoader pmmp plugin](presentkim-pm/UnicodeFontLoader)

Ever felt bothered managing emoji fonts for the server?
Or struggled finding emoji Unicode?
Try this plugin!

This plugin automatically bundles font files located in the `resource_packs/font` directory into a resourcepack.

> For detailed information about image fonts, please
> visit [bedrock.dev/concepts/emojis](https://wiki.bedrock.dev/concepts/emojis.html)

:heavy_check_mark: Automatically generates font resource pack based on character images (
like `packs/fonts/glyph_XX/YY.png`).    
:heavy_check_mark: Registers the generated resource pack on the server  
:heavy_check_mark: Separates and applies existing font glyph files (`glyph_**.png`) on `packs/fonts` directory.  
:heavy_check_mark: All results are cached to optimize repetitive tasks

##

-----

#### Usage

1. When you apply the plugin and start the server, the default unicode font images are generated as examples in
   the `waterdog/packs/fonts` directory.
2. If a file named `.../fonts/glyph_XX/YY.png` exists, it will automatically be applied as the font corresponding to
   Unicode `U+XXYY`.
3. If you want to convert existing font files to fit the plugin, simply place the 'glyph_XX.png' file in the 'font'
   directory, and it will be automatically converted.

The automatic conversion and build feature runs once when the server starts up.
Restart is required for the changes to take effect after modification.

When using this image font on the server, simply input the corresponding character for 'U+XXYY' as usual.  
The Unicode converter can be conveniently accessed
via [bedrock.dev](https://wiki.bedrock.dev/concepts/emojis.html#hexValue)
or [unicodeconverter.net](https://unicodeconverter.net/).


> exmaple) When server directory is configured as below,
>
> ```bash
> . # waterdog directory
> ├── packs
> │   └── fonts
> |       ├── glyph_E0
> |       |   ├──── 01.png
> |       |   ├──── 0A.png
> |       |   └──── ...
> |       |
> │       └──── glyph_E3.png
> │
> └── ...
> ```
>
> In that case, the 'glyph_E3.png' file will automatically be split into 'glyph_E3/YY.png' and applied to the server.  
> Additionally, the existing 'glyph_E3.png' file will be removed.
>
> ```bash
> . # waterdog directory
> ├── packs
> │   └── fonts
> |       ├── glyph_E0
> |       |   ├──── 01.png
> |       |   ├──── 0A.png
> |       |   └──── ...
> |       |
> │       └─── glyph_E3
> |       |   ├──── 0A.png
> |       |   ├──── 0B.png
> |       |   └──── ...
> │
> └── ...
> ```


##

## Target software:

This plugin officially only works with [`WaterDog`](https://waterdog.dev/).

##

-----

## Downloads

### Download from [Github Releases][releases-url]

[![Github Downloads][release-badge]][releases-url]

##

-----

## License

Distributed under the **LGPL 3.0**. See [LICENSE][license-url] for more information

##

-----

[author-discord]: https://discordapp.com/users/345772340279508993

[version-badge]: https://img.shields.io/github/v/release/presentkim-pm/UnicodeFontLoader-WDPE?display_name=tag&style=for-the-badge&label=VERSION

[release-badge]: https://img.shields.io/github/downloads/presentkim-pm/UnicodeFontLoader-WDPE/total?style=for-the-badge&label=GITHUB%20

[stars-badge]: https://img.shields.io/github/stars/presentkim-pm/UnicodeFontLoader-WDPE.svg?style=for-the-badge

[license-badge]: https://img.shields.io/github/license/presentkim-pm/UnicodeFontLoader-WDPE.svg?style=for-the-badge

[stars-url]: https://github.com/presentkim-pm/UnicodeFontLoader-WDPE/stargazers

[releases-url]: https://github.com/presentkim-pm/UnicodeFontLoader-WDPE/releases

[issues-url]: https://github.com/presentkim-pm/UnicodeFontLoader-WDPE/issues

[license-url]: https://github.com/presentkim-pm/UnicodeFontLoader-WDPE/blob/main/LICENSE

[project-icon]: https://raw.githubusercontent.com/presentkim-pm/UnicodeFontLoader/main/assets/icon.png

[project-preview]: https://raw.githubusercontent.com/presentkim-pm/UnicodeFontLoader/main/assets/preview.gif
