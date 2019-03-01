package reactjs.theme

import scala.scalajs.js

@js.native
trait MuiColor extends js.Object
@js.native
trait MuiTheme extends js.Object

@js.native
trait MuiStyles extends js.Object {
  def getMuiTheme(raw: MuiRawTheme): MuiTheme = js.native
  val AutoPrefix: js.Dynamic                  = js.native
  val colors: MuiColors                       = js.native
  val Spacing: MuiSpacings                    = js.native
  val MuiThemeProvider: js.Dynamic            = js.native
  val Typography: js.Dynamic                  = js.native
  val Transitions: js.Dynamic                 = js.native
  val DarkRawTheme: MuiRawTheme               = js.native
  val LightRawTheme: MuiRawTheme              = js.native
}

@js.native
trait MuiSpacings extends js.Object {
  val iconSize: Int                      = js.native
  val desktopGutter: Int                 = js.native
  val desktopGutterMore: Int             = js.native
  val desktopGutterLess: Int             = js.native
  val desktopGutterMini: Int             = js.native
  val desktopKeylineIncrement: Int       = js.native
  val desktopDropDownMenuItemHeight: Int = js.native
  val desktopDropDownMenuFontSize: Int   = js.native
  val desktopLeftNavMenuItemHeight: Int  = js.native
  val desktopSubheaderHeight: Int        = js.native
  val desktopToolbarHeight: Int          = js.native
}

object MuiSpacings {
  def apply(iconSize: Int,
            desktopGutter: Int,
            desktopGutterMore: Int,
            desktopGutterLess: Int,
            desktopGutterMini: Int,
            desktopKeylineIncrement: Int,
            desktopDropDownMenuItemHeight: Int,
            desktopDropDownMenuFontSize: Int,
            desktopLeftNavMenuItemHeight: Int,
            desktopSubheaderHeight: Int,
            desktopToolbarHeight: Int): MuiSpacings =
    js.Dynamic
      .literal(
        iconSize = iconSize,
        desktopGutter = desktopGutter,
        desktopGutterMore = desktopGutterMore,
        desktopGutterLess = desktopGutterLess,
        desktopGutterMini = desktopGutterMini,
        desktopKeylineIncrement = desktopKeylineIncrement,
        desktopDropDownMenuItemHeight = desktopDropDownMenuItemHeight,
        desktopDropDownMenuFontSize = desktopDropDownMenuFontSize,
        desktopLeftNavMenuItemHeight = desktopLeftNavMenuItemHeight,
        desktopSubheaderHeight = desktopSubheaderHeight,
        desktopToolbarHeight = desktopToolbarHeight
      )
      .asInstanceOf[MuiSpacings]

  implicit class MuiSpacingOps(s: MuiSpacings) {
    def copy(iconSize: Int = s.iconSize,
             desktopGutter: Int = s.desktopGutter,
             desktopGutterMore: Int = s.desktopGutterMore,
             desktopGutterLess: Int = s.desktopGutterLess,
             desktopGutterMini: Int = s.desktopGutterMini,
             desktopKeylineIncrement: Int = s.desktopKeylineIncrement,
             desktopDropDownMenuItemHeight: Int = s.desktopDropDownMenuItemHeight,
             desktopDropDownMenuFontSize: Int = s.desktopDropDownMenuFontSize,
             desktopLeftNavMenuItemHeight: Int = s.desktopLeftNavMenuItemHeight,
             desktopSubheaderHeight: Int = s.desktopSubheaderHeight,
             desktopToolbarHeight: Int = s.desktopToolbarHeight): MuiSpacings =
      js.Dynamic
        .literal(
          iconSize = iconSize,
          desktopGutter = desktopGutter,
          desktopGutterMore = desktopGutterMore,
          desktopGutterLess = desktopGutterLess,
          desktopGutterMini = desktopGutterMini,
          desktopKeylineIncrement = desktopKeylineIncrement,
          desktopDropDownMenuItemHeight = desktopDropDownMenuItemHeight,
          desktopDropDownMenuFontSize = desktopDropDownMenuFontSize,
          desktopLeftNavMenuItemHeight = desktopLeftNavMenuItemHeight,
          desktopSubheaderHeight = desktopSubheaderHeight,
          desktopToolbarHeight = desktopToolbarHeight
        )
        .asInstanceOf[MuiSpacings]
  }
}

@js.native
trait MuiPalette extends js.Object {
  val primary1Color: MuiColor      = js.native
  val primary2Color: MuiColor      = js.native
  val primary3Color: MuiColor      = js.native
  val accent1Color: MuiColor       = js.native
  val accent2Color: MuiColor       = js.native
  val accent3Color: MuiColor       = js.native
  val textColor: MuiColor          = js.native
  val alternateTextColor: MuiColor = js.native
  val canvasColor: MuiColor        = js.native
  val borderColor: MuiColor        = js.native
  val disabledColor: MuiColor      = js.native
}

object MuiPalette {
  def apply(primary1Color: MuiColor,
            primary2Color: MuiColor,
            primary3Color: MuiColor,
            accent1Color: MuiColor,
            accent2Color: MuiColor,
            accent3Color: MuiColor,
            textColor: MuiColor,
            alternateTextColor: MuiColor,
            canvasColor: MuiColor,
            borderColor: MuiColor,
            disabledColor: MuiColor): MuiPalette = {
    js.Dynamic
      .literal(
        primary1Color = primary1Color,
        primary2Color = primary2Color,
        primary3Color = primary3Color,
        accent1Color = accent1Color,
        accent2Color = accent2Color,
        accent3Color = accent3Color,
        textColor = textColor,
        alternateTextColor = alternateTextColor,
        canvasColor = canvasColor,
        borderColor = borderColor,
        disabledColor = disabledColor
      )
      .asInstanceOf[MuiPalette]
  }

  implicit class MuiPaletteOps(p: MuiPalette) {
    def copy(primary1Color: MuiColor = p.primary1Color,
             primary2Color: MuiColor = p.primary2Color,
             primary3Color: MuiColor = p.primary3Color,
             accent1Color: MuiColor = p.accent1Color,
             accent2Color: MuiColor = p.accent2Color,
             accent3Color: MuiColor = p.accent3Color,
             textColor: MuiColor = p.textColor,
             alternateTextColor: MuiColor = p.alternateTextColor,
             canvasColor: MuiColor = p.canvasColor,
             borderColor: MuiColor = p.borderColor,
             disabledColor: MuiColor = p.disabledColor): MuiPalette =
      js.Dynamic
        .literal(
          primary1Color = primary1Color,
          primary2Color = primary2Color,
          primary3Color = primary3Color,
          accent1Color = accent1Color,
          accent2Color = accent2Color,
          accent3Color = accent3Color,
          textColor = textColor,
          alternateTextColor = alternateTextColor,
          canvasColor = canvasColor,
          borderColor = borderColor,
          disabledColor = disabledColor
        )
        .asInstanceOf[MuiPalette]
  }
}

@js.native
trait MuiRawTheme extends js.Object {
  val spacing: MuiSpacings = js.native
  val fontFamily: String   = js.native
  val palette: MuiPalette  = js.native
}

object MuiRawTheme {
  def apply(spacing: MuiSpacings, fontFamily: String, palette: MuiPalette): MuiRawTheme = {
    js.Dynamic
      .literal(spacing = spacing, fontFamily = fontFamily, palette = palette)
      .asInstanceOf[MuiRawTheme]
  }

  implicit class MuiRawThemeOps(t: MuiRawTheme) {
    def copy(spacing: MuiSpacings = t.spacing,
             fontFamily: String = t.fontFamily,
             palette: MuiPalette = t.palette): MuiRawTheme =
      js.Dynamic
        .literal(
          spacing = spacing,
          fontFamily = fontFamily,
          palette = palette
        )
        .asInstanceOf[MuiRawTheme]
  }
}

@js.native
trait MuiColors extends js.Object {
  val red50: MuiColor          = js.native
  val red100: MuiColor         = js.native
  val red200: MuiColor         = js.native
  val red300: MuiColor         = js.native
  val red400: MuiColor         = js.native
  val red500: MuiColor         = js.native
  val red600: MuiColor         = js.native
  val red700: MuiColor         = js.native
  val red800: MuiColor         = js.native
  val red900: MuiColor         = js.native
  val redA100: MuiColor        = js.native
  val redA200: MuiColor        = js.native
  val redA400: MuiColor        = js.native
  val redA700: MuiColor        = js.native
  val pink50: MuiColor         = js.native
  val pink100: MuiColor        = js.native
  val pink200: MuiColor        = js.native
  val pink300: MuiColor        = js.native
  val pink400: MuiColor        = js.native
  val pink500: MuiColor        = js.native
  val pink600: MuiColor        = js.native
  val pink700: MuiColor        = js.native
  val pink800: MuiColor        = js.native
  val pink900: MuiColor        = js.native
  val pinkA100: MuiColor       = js.native
  val pinkA200: MuiColor       = js.native
  val pinkA400: MuiColor       = js.native
  val pinkA700: MuiColor       = js.native
  val purple50: MuiColor       = js.native
  val purple100: MuiColor      = js.native
  val purple200: MuiColor      = js.native
  val purple300: MuiColor      = js.native
  val purple400: MuiColor      = js.native
  val purple500: MuiColor      = js.native
  val purple600: MuiColor      = js.native
  val purple700: MuiColor      = js.native
  val purple800: MuiColor      = js.native
  val purple900: MuiColor      = js.native
  val purpleA100: MuiColor     = js.native
  val purpleA200: MuiColor     = js.native
  val purpleA400: MuiColor     = js.native
  val purpleA700: MuiColor     = js.native
  val deepPurple50: MuiColor   = js.native
  val deepPurple100: MuiColor  = js.native
  val deepPurple200: MuiColor  = js.native
  val deepPurple300: MuiColor  = js.native
  val deepPurple400: MuiColor  = js.native
  val deepPurple500: MuiColor  = js.native
  val deepPurple600: MuiColor  = js.native
  val deepPurple700: MuiColor  = js.native
  val deepPurple800: MuiColor  = js.native
  val deepPurple900: MuiColor  = js.native
  val deepPurpleA100: MuiColor = js.native
  val deepPurpleA200: MuiColor = js.native
  val deepPurpleA400: MuiColor = js.native
  val deepPurpleA700: MuiColor = js.native
  val indigo50: MuiColor       = js.native
  val indigo100: MuiColor      = js.native
  val indigo200: MuiColor      = js.native
  val indigo300: MuiColor      = js.native
  val indigo400: MuiColor      = js.native
  val indigo500: MuiColor      = js.native
  val indigo600: MuiColor      = js.native
  val indigo700: MuiColor      = js.native
  val indigo800: MuiColor      = js.native
  val indigo900: MuiColor      = js.native
  val indigoA100: MuiColor     = js.native
  val indigoA200: MuiColor     = js.native
  val indigoA400: MuiColor     = js.native
  val indigoA700: MuiColor     = js.native
  val blue50: MuiColor         = js.native
  val blue100: MuiColor        = js.native
  val blue200: MuiColor        = js.native
  val blue300: MuiColor        = js.native
  val blue400: MuiColor        = js.native
  val blue500: MuiColor        = js.native
  val blue600: MuiColor        = js.native
  val blue700: MuiColor        = js.native
  val blue800: MuiColor        = js.native
  val blue900: MuiColor        = js.native
  val blueA100: MuiColor       = js.native
  val blueA200: MuiColor       = js.native
  val blueA400: MuiColor       = js.native
  val blueA700: MuiColor       = js.native
  val lightBlue50: MuiColor    = js.native
  val lightBlue100: MuiColor   = js.native
  val lightBlue200: MuiColor   = js.native
  val lightBlue300: MuiColor   = js.native
  val lightBlue400: MuiColor   = js.native
  val lightBlue500: MuiColor   = js.native
  val lightBlue600: MuiColor   = js.native
  val lightBlue700: MuiColor   = js.native
  val lightBlue800: MuiColor   = js.native
  val lightBlue900: MuiColor   = js.native
  val lightBlueA100: MuiColor  = js.native
  val lightBlueA200: MuiColor  = js.native
  val lightBlueA400: MuiColor  = js.native
  val lightBlueA700: MuiColor  = js.native
  val cyan50: MuiColor         = js.native
  val cyan100: MuiColor        = js.native
  val cyan200: MuiColor        = js.native
  val cyan300: MuiColor        = js.native
  val cyan400: MuiColor        = js.native
  val cyan500: MuiColor        = js.native
  val cyan600: MuiColor        = js.native
  val cyan700: MuiColor        = js.native
  val cyan800: MuiColor        = js.native
  val cyan900: MuiColor        = js.native
  val cyanA100: MuiColor       = js.native
  val cyanA200: MuiColor       = js.native
  val cyanA400: MuiColor       = js.native
  val cyanA700: MuiColor       = js.native
  val teal50: MuiColor         = js.native
  val teal100: MuiColor        = js.native
  val teal200: MuiColor        = js.native
  val teal300: MuiColor        = js.native
  val teal400: MuiColor        = js.native
  val teal500: MuiColor        = js.native
  val teal600: MuiColor        = js.native
  val teal700: MuiColor        = js.native
  val teal800: MuiColor        = js.native
  val teal900: MuiColor        = js.native
  val tealA100: MuiColor       = js.native
  val tealA200: MuiColor       = js.native
  val tealA400: MuiColor       = js.native
  val tealA700: MuiColor       = js.native
  val green50: MuiColor        = js.native
  val green100: MuiColor       = js.native
  val green200: MuiColor       = js.native
  val green300: MuiColor       = js.native
  val green400: MuiColor       = js.native
  val green500: MuiColor       = js.native
  val green600: MuiColor       = js.native
  val green700: MuiColor       = js.native
  val green800: MuiColor       = js.native
  val green900: MuiColor       = js.native
  val greenA100: MuiColor      = js.native
  val greenA200: MuiColor      = js.native
  val greenA400: MuiColor      = js.native
  val greenA700: MuiColor      = js.native
  val lightGreen50: MuiColor   = js.native
  val lightGreen100: MuiColor  = js.native
  val lightGreen200: MuiColor  = js.native
  val lightGreen300: MuiColor  = js.native
  val lightGreen400: MuiColor  = js.native
  val lightGreen500: MuiColor  = js.native
  val lightGreen600: MuiColor  = js.native
  val lightGreen700: MuiColor  = js.native
  val lightGreen800: MuiColor  = js.native
  val lightGreen900: MuiColor  = js.native
  val lightGreenA100: MuiColor = js.native
  val lightGreenA200: MuiColor = js.native
  val lightGreenA400: MuiColor = js.native
  val lightGreenA700: MuiColor = js.native
  val lime50: MuiColor         = js.native
  val lime100: MuiColor        = js.native
  val lime200: MuiColor        = js.native
  val lime300: MuiColor        = js.native
  val lime400: MuiColor        = js.native
  val lime500: MuiColor        = js.native
  val lime600: MuiColor        = js.native
  val lime700: MuiColor        = js.native
  val lime800: MuiColor        = js.native
  val lime900: MuiColor        = js.native
  val limeA100: MuiColor       = js.native
  val limeA200: MuiColor       = js.native
  val limeA400: MuiColor       = js.native
  val limeA700: MuiColor       = js.native
  val yellow50: MuiColor       = js.native
  val yellow100: MuiColor      = js.native
  val yellow200: MuiColor      = js.native
  val yellow300: MuiColor      = js.native
  val yellow400: MuiColor      = js.native
  val yellow500: MuiColor      = js.native
  val yellow600: MuiColor      = js.native
  val yellow700: MuiColor      = js.native
  val yellow800: MuiColor      = js.native
  val yellow900: MuiColor      = js.native
  val yellowA100: MuiColor     = js.native
  val yellowA200: MuiColor     = js.native
  val yellowA400: MuiColor     = js.native
  val yellowA700: MuiColor     = js.native
  val amber50: MuiColor        = js.native
  val amber100: MuiColor       = js.native
  val amber200: MuiColor       = js.native
  val amber300: MuiColor       = js.native
  val amber400: MuiColor       = js.native
  val amber500: MuiColor       = js.native
  val amber600: MuiColor       = js.native
  val amber700: MuiColor       = js.native
  val amber800: MuiColor       = js.native
  val amber900: MuiColor       = js.native
  val amberA100: MuiColor      = js.native
  val amberA200: MuiColor      = js.native
  val amberA400: MuiColor      = js.native
  val amberA700: MuiColor      = js.native
  val orange50: MuiColor       = js.native
  val orange100: MuiColor      = js.native
  val orange200: MuiColor      = js.native
  val orange300: MuiColor      = js.native
  val orange400: MuiColor      = js.native
  val orange500: MuiColor      = js.native
  val orange600: MuiColor      = js.native
  val orange700: MuiColor      = js.native
  val orange800: MuiColor      = js.native
  val orange900: MuiColor      = js.native
  val orangeA100: MuiColor     = js.native
  val orangeA200: MuiColor     = js.native
  val orangeA400: MuiColor     = js.native
  val orangeA700: MuiColor     = js.native
  val deepOrange50: MuiColor   = js.native
  val deepOrange100: MuiColor  = js.native
  val deepOrange200: MuiColor  = js.native
  val deepOrange300: MuiColor  = js.native
  val deepOrange400: MuiColor  = js.native
  val deepOrange500: MuiColor  = js.native
  val deepOrange600: MuiColor  = js.native
  val deepOrange700: MuiColor  = js.native
  val deepOrange800: MuiColor  = js.native
  val deepOrange900: MuiColor  = js.native
  val deepOrangeA100: MuiColor = js.native
  val deepOrangeA200: MuiColor = js.native
  val deepOrangeA400: MuiColor = js.native
  val deepOrangeA700: MuiColor = js.native
  val brown50: MuiColor        = js.native
  val brown100: MuiColor       = js.native
  val brown200: MuiColor       = js.native
  val brown300: MuiColor       = js.native
  val brown400: MuiColor       = js.native
  val brown500: MuiColor       = js.native
  val brown600: MuiColor       = js.native
  val brown700: MuiColor       = js.native
  val brown800: MuiColor       = js.native
  val brown900: MuiColor       = js.native
  val blueGrey50: MuiColor     = js.native
  val blueGrey100: MuiColor    = js.native
  val blueGrey200: MuiColor    = js.native
  val blueGrey300: MuiColor    = js.native
  val blueGrey400: MuiColor    = js.native
  val blueGrey500: MuiColor    = js.native
  val blueGrey600: MuiColor    = js.native
  val blueGrey700: MuiColor    = js.native
  val blueGrey800: MuiColor    = js.native
  val blueGrey900: MuiColor    = js.native
  val grey50: MuiColor         = js.native
  val grey100: MuiColor        = js.native
  val grey200: MuiColor        = js.native
  val grey300: MuiColor        = js.native
  val grey400: MuiColor        = js.native
  val grey500: MuiColor        = js.native
  val grey600: MuiColor        = js.native
  val grey700: MuiColor        = js.native
  val grey800: MuiColor        = js.native
  val grey900: MuiColor        = js.native
  val black: MuiColor          = js.native
  val white: MuiColor          = js.native
  val transparent: MuiColor    = js.native
  val fullBlack: MuiColor      = js.native
  val darkBlack: MuiColor      = js.native
  val lightBlack: MuiColor     = js.native
  val minBlack: MuiColor       = js.native
  val faintBlack: MuiColor     = js.native
  val fullWhite: MuiColor      = js.native
  val darkWhite: MuiColor      = js.native
  val lightWhite: MuiColor     = js.native
}