# Compose-Elements

[![Maven Central](https://img.shields.io/badge/version-1.1.1-blue)](https://github.com/rgryta/Compose-Elements/releases)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-1.9.3-blue.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

A **Kotlin Multiplatform** library providing commonly used Jetpack Compose UI components for building cross-platform applications. Compose-Elements offers reusable, customizable components with support for Android, iOS, Desktop (JVM), JavaScript, and WebAssembly.

## Features

- **DateList** - Horizontally scrollable date picker with infinite scroll
- **InfiniteList** - Generic infinite scrolling lazy row component
- **Loading** - Animated loading indicator with customizable styling
- **Theme System** - Material 3 theme with light/dark mode support
- **Multiplatform** - Works on Android, iOS, Desktop, Web, and WASM
- **Customizable** - Extensive customization options for colors, shapes, and spacing
- **Localized** - Built-in support for multiple languages (English, Polish)

## Platform Support

| Platform | Status |
|----------|--------|
| Android (API 24+) | ✅ Supported |
| iOS (arm64, x64, simulator) | ✅ Supported |
| JVM/Desktop | ✅ Supported |
| JavaScript | ✅ Supported |
| WebAssembly (WASM) | ✅ Supported |

## Installation

### Gradle (Kotlin DSL)

Add the GitHub Packages repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/rgryta/Compose-Elements")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_TOKEN")
            }
        }
    }
}
```

Add the dependency to your module's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("eu.gryta:compose.elements:1.1.1")
        }
    }
}
```

### GitHub Authentication

To access GitHub Packages, you need a Personal Access Token (PAT) with `read:packages` permission.

1. Create a PAT at: https://github.com/settings/tokens
2. Add to your `gradle.properties`:

```properties
gpr.user=your-github-username
gpr.key=your-github-token
```

Or set environment variables:

```bash
export GPR_USERNAME=your-github-username
export GPR_TOKEN=your-github-token
```

## Components

### DateList

A horizontally scrollable date picker with infinite scroll capability.

```kotlin
import eu.gryta.compose.elements.datelist.DateList
import kotlinx.datetime.LocalDate

@Composable
fun MyDatePicker() {
    DateList(
        selectedDate = LocalDate(2025, 1, 20),
        onDateSelect = { newDate, previousDate ->
            println("Selected: $newDate, Previous: $previousDate")
        },
        initialItemsCount = 50, // Number of dates to display initially
        loadMoreThreshold = 5,  // Load more when within 5 items from the end
        itemSpacing = 8.dp
    )
}
```

#### Customization

```kotlin
DateList(
    selectedDate = today,
    onDateSelect = { newDate, _ ->
        viewModel.updateSelectedDate(newDate)
    },
    cardShape = RoundedCornerShape(16.dp),
    colors = DateListDefaults.colors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        unselectedContainerColor = MaterialTheme.colorScheme.surface,
        selectedContentColor = MaterialTheme.colorScheme.onPrimary,
        unselectedContentColor = MaterialTheme.colorScheme.onSurface
    )
)
```

#### Custom Item Content

```kotlin
DateList(
    selectedDate = today,
    onDateSelect = { newDate, _ -> /* handle selection */ },
    itemContent = { date, selected, onClick ->
        // Your custom date card implementation
        CustomDateCard(date, selected, onClick)
    }
)
```

### GenericInfiniteLazyRow

A reusable generic component for building infinite-scroll horizontal lists.

```kotlin
import eu.gryta.compose.elements.infinitelist.GenericInfiniteLazyRow
import eu.gryta.compose.elements.infinitelist.rememberInfiniteListState

data class MyItem(val id: Int, val name: String)

@Composable
fun MyInfiniteList() {
    val state = rememberInfiniteListState(
        initialItems = listOf(
            MyItem(1, "Item 1"),
            MyItem(2, "Item 2"),
            MyItem(3, "Item 3")
        ),
        loadMore = { currentItems ->
            // Load more items from network/database
            val lastId = currentItems.last().id
            fetchMoreItems(startId = lastId + 1)
        }
    )

    GenericInfiniteLazyRow(
        state = state,
        selectedIndex = 0,
        onItemSelect = { selected, previous ->
            println("Selected: ${selected.name}")
        },
        itemSpacing = 12.dp,
        loadMoreThreshold = 3,
        keySelector = { it.id }
    ) { item, onItemClick ->
        // Your custom item UI
        Card(
            onClick = onItemClick,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(item.name)
        }
    }
}
```

### Loading Indicator

An animated loading indicator with pulsing circles.

```kotlin
import eu.gryta.compose.elements.generics.Loading

@Composable
fun MyLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Loading(
            circleColor = MaterialTheme.colorScheme.primary,
            circleSize = 48.dp,
            animationDelay = 400,
            initialAlpha = 0.3f
        )
    }
}
```

### Theme

Material 3 theme with automatic light/dark mode detection.

```kotlin
import eu.gryta.compose.elements.theme.AppTheme

@Composable
fun App() {
    AppTheme(
        darkTheme = isSystemInDarkTheme()
        // Optional: colorScheme = myCustomColorScheme
    ) {
        // Your app content
        MyScreen()
    }
}
```

#### Custom Color Scheme

```kotlin
AppTheme(
    colorScheme = darkColorScheme(
        primary = Color(0xFF6CDBAC),
        secondary = Color(0xFFB9C9C2),
        tertiary = Color(0xFFA1CDE1)
    )
) {
    MyScreen()
}
```

## Localization

The library includes localized day names for:

- **English** (en)
- **Polish** (pl)

Day names automatically adapt to the system locale. To add more languages, contribute to the project or request support.

## Requirements

- **Kotlin**: 2.3.0+
- **Compose Multiplatform**: 1.9.3+
- **Android**: minSdk 24, compileSdk 36
- **JVM**: Java 21

## Building from Source

```bash
# Clone the repository
git clone https://github.com/rgryta/Compose-Elements.git
cd Compose-Elements

# Build the library
make build

# Run tests
make test

# Publish to Maven Local
make publish
```

## Testing

The library includes comprehensive unit tests for core logic:

```bash
./gradlew test
```

Test coverage includes:
- `InfiniteListState` logic (10 tests)
- Color definitions and contrasts (10 tests)

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Versioning

This project uses [Semantic Versioning](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for new functionality (backwards compatible)
- **PATCH** version for bug fixes

Current version: **1.1.1**

## License

This project is licensed under the **GNU General Public License v3.0** - see the [LICENSE](LICENSE) file for details.

## Author

**Radosław Gryta**

- GitHub: [@rgryta](https://github.com/rgryta)
- Email: radek.gryta@gmail.com

## Links

- [Repository](https://github.com/rgryta/Compose-Elements)
- [Issues](https://github.com/rgryta/Compose-Elements/issues)
- [Releases](https://github.com/rgryta/Compose-Elements/releases)
- [GitHub Packages](https://github.com/rgryta/Compose-Elements/packages)

## Changelog

### [1.1.1] - 2025-01-20
- Fix Makefile to enable configuration cache in CI publish workflow

### [1.1.0] - 2025-01-05
- Add comprehensive KDoc documentation for all public APIs
- Add error handling and loading states to InfiniteListState
- Add performance optimizations and accessibility support
- Add missing component features

### [1.0.2] - 2025-01-03
- Release with customizable composables

### [1.0.1]
- Previous release

### [1.0.0]
- Initial release with DateList, InfiniteList, Loading, and Theme components

---

Made with ❤️ using Kotlin Multiplatform and Jetpack Compose
