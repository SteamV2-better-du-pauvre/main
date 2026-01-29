# Database Loader

A Java application that generates and loads test data into `editor_db` and `platform_db` databases using **Datafaker** library.

## Overview

This tool is designed to populate two PostgreSQL databases with realistic test data for a Steam-like asynchronous platform using Kafka. It generates:

- **Editors** (game publishers - individuals and companies)
- **Games** (with platforms and genres)
- **DLCs** (downloadable content)
- **Patches** (game updates)
- **Bug Reports** (user-submitted issues)
- **Evaluations** (game reviews with ratings)

The loader simulates the synchronization between `editor_db` (editor's internal database) and `platform_db` (public platform database) by only syncing published content.

## Project Structure

```
DatabaseLoader/
├── config.properties           # Configuration file (customize here!)
├── src/
│   ├── Main.java              # Entry point
│   ├── config/
│   │   └── DatabaseConfig.java
│   ├── connection/
│   │   └── DatabaseConnection.java
│   ├── generators/            # Data generators using Datafaker
│   │   ├── EditorGenerator.java
│   │   ├── GameGenerator.java
│   │   ├── DLCGenerator.java
│   │   ├── PatchGenerator.java
│   │   ├── BugReportGenerator.java
│   │   └── EvaluationGenerator.java
│   ├── loaders/               # Database loaders
│   │   ├── EditorDbLoader.java
│   │   └── PlatformDbLoader.java
│   └── utils/
│       └── FakerProvider.java
└── lib/                       # Dependencies (JAR files)
    ├── datafaker-x.x.x.jar
    └── postgresql-x.x.x.jar
```

## Prerequisites

1. **Java 17+** (uses modern Java features)
2. **PostgreSQL** (running with `editor_db` and `platform_db` databases)
3. **Dependencies**:
   - Datafaker library ([download here](https://github.com/datafaker-net/datafaker))
   - PostgreSQL JDBC Driver ([download here](https://jdbc.postgresql.org/download/))

## Setup

### 1. Download Dependencies

Place the following JAR files in the `lib/` directory:

```bash
# Navigate to the lib directory
cd database/DatabaseLoader/lib/

# Download Datafaker (example - adjust version)
wget https://repo1.maven.org/maven2/net/datafaker/datafaker/2.0.2/datafaker-2.0.2.jar

# Download PostgreSQL JDBC Driver
wget https://jdbc.postgresql.org/download/postgresql-42.7.1.jar
```

### 2. Configure Database Connection

Edit `config.properties`:

```properties
# Database connection
db.url=jdbc:postgresql://localhost:5432/
db.user=user
db.password=password
```

### 3. Customize Data Volume

Adjust the number of entries to generate in `config.properties`:

```properties
# Number of editors
num.editors=20

# Games per editor (random between min and max)
num.games.per.editor.min=3
num.games.per.editor.max=7

# DLCs per game
num.dlc.per.game.min=0
num.dlc.per.game.max=3

# Patches per game
num.patches.per.game.min=2
num.patches.per.game.max=5

# Bug reports per game
num.bug.reports.per.game.min=1
num.bug.reports.per.game.max=10

# Evaluations per game
num.evaluations.per.game.min=5
num.evaluations.per.game.max=50
```

## Compilation

### Option 1: Using Command Line

```bash
# Navigate to DatabaseLoader directory
cd database/DatabaseLoader

# Compile all Java files
javac -cp "lib/*" -d out src/**/*.java src/*.java

# Copy config.properties to output directory
cp config.properties out/
```

### Option 2: Using IntelliJ IDEA

1. Open the `DatabaseLoader` project in IntelliJ
2. Add JAR files from `lib/` to Project Structure → Libraries
3. Build the project (Build → Build Project)

## Running

### Command Line

```bash
# From DatabaseLoader directory
java -cp "out:lib/*" Main
```

### IntelliJ IDEA

Simply run the `Main` class (Shift + F10)

## Configuration Options

### Data Generation Settings

```properties
# Percentage of editors that are "entreprise" (rest are "particulier")
editor.enterprise.percentage=80

# Percentage of games/dlc/patches that are published
publish.percentage=80
```

### Loader Settings

```properties
# Clear all tables before loading new data
clear.tables.before.load=true

# Print detailed progress information
verbose=true
```

## Generated Data Examples

### Editors
- Companies: "Rockstar Games", "Nintendo Studios", "Ubisoft Entertainment"
- Individuals: "John Doe", "Jane Smith"

### Games
- Titles: "The Legend of Zelda: Breath of the Wild", "Galaxy Warriors", "Cat Simulator"
- Platforms: PC, XBOX, PS5, SWITCH (1-4 per game)
- Genres: ACTION, RPG, STRATEGY, SPORTS (1-3 per game)

### Patches
- Realistic version progression: 1.0 → 1.3 → 2.1
- Comments: "Critical bug fixes and performance improvements"
- Modifications: Detailed list of changes

### Evaluations
- Ratings: 0-10 (bell curve distribution, most 6-9)
- Reviews: Context-appropriate based on rating

## Data Flow

```
1. editor_db (Editor's Database)
   ├─ All editors
   ├─ All games (published + unpublished)
   ├─ All DLCs (published + unpublished)
   ├─ All patches (published + unpublished)
   ├─ Bug reports (synced from platform)
   └─ Evaluations (synced from platform)

2. platform_db (Public Platform)
   ├─ All editors (replicated)
   ├─ Published games only
   ├─ Published DLCs only
   └─ Published patches only
```

## Troubleshooting

### Connection Issues

```
Error: Connection refused
```
**Solution**: Ensure PostgreSQL is running and databases exist:
```sql
CREATE DATABASE editor_db;
CREATE DATABASE platform_db;
```

### ClassNotFoundException

```
Error: org.postgresql.Driver not found
```
**Solution**: Verify PostgreSQL JDBC driver is in `lib/` directory and included in classpath.

### OutOfMemoryError

```
Error: Java heap space
```
**Solution**: Increase heap size when running:
```bash
java -Xmx2g -cp "out:lib/*" Main
```

## Datafaker Providers Used

This project uses the following Datafaker providers:

- **Company**: Company names for enterprise editors
- **Name**: Person names for individual editors
- **Internet**: Passwords, domains
- **Lorem**: Descriptions, paragraphs
- **VideoGame**: Game titles, platforms
- **Commerce**: Prices
- **Number**: Versions, ratings
- **Ancient**: Gods, heroes (for game names)
- **Space**: Galaxies, planets
- **Animal**: Animals (for game names)
- **Adjective/Verb**: Modifiers
- **And many more!**

## Customization

### Adding New Generators

1. Create a new generator class in `generators/`
2. Implement generation logic using Datafaker
3. Add to `EditorDbLoader` or `PlatformDbLoader`

### Modifying Data Distributions

Edit the generator classes to adjust:
- Rating distributions (in `EvaluationGenerator`)
- Price ranges (in `GameGenerator`, `DLCGenerator`)
- Version increments (in `PatchGenerator`)

## Performance

Approximate loading times (on standard hardware):

| Editors | Games | Total Time |
|---------|-------|------------|
| 10      | ~50   | ~2 seconds |
| 20      | ~100  | ~5 seconds |
| 100     | ~500  | ~20 seconds |
| 500     | ~2500 | ~2 minutes |

## License

This project is part of a JVM course at Polytech.

## Author

Moua - Polytech Ingé 2 - S1 JVM Project
