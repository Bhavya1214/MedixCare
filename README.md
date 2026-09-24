# MedixCare — Hospital Management System

MedixCare is a Java + MySQL hospital management system with **two independent
front ends** over the same kind of domain (patients, doctors, staff, rooms,
appointments, billing):

| App | Entry point | Interface |
|---|---|---|
| **GUI** | `com.medixcare.gui.MedixCare` | JavaFX desktop app |
| **CLI** | `com.medixcare.cli.MedixCareSystem` | Console menu (also covers Medical Records) |

They were originally written as two separate, standalone files against two
different schemas (`HMS` for the GUI, `MedixCare` for the CLI, with different
table-naming conventions). This repo keeps them as **separate modules** in one
Maven project rather than merging them, since merging would mean rewriting one
app's data layer to match the other's schema.

## Features

**Both apps** manage:
- Patients — add, view, update, search, delete
- Doctors & Staff
- Rooms (assignment/occupancy)
- Appointments
- Billing

**CLI only:** Medical Records management, plus a fully normalized schema with
explicit relationship tables (`Has_Appointment`, `Handled_By`, `Occupies`, etc.).

**GUI only:** JavaFX forms/tables for a point-and-click desktop experience.

## Project structure

```
medixcare/
├── pom.xml
├── README.md
├── LICENSE
├── .gitignore
├── src/main/java/com/medixcare/
│   ├── gui/MedixCare.java           # JavaFX app
│   ├── cli/MedixCareSystem.java     # Console app
│   └── config/DatabaseConfig.java   # Shared DB connection settings
└── src/main/resources/
    ├── config.properties.example    # Copy to config.properties and fill in
    ├── schema-gui.sql               # Schema used by the GUI app (HMS db)
    └── schema-cli.sql               # Schema used by the CLI app (MedixCare db)
```

## Prerequisites

- JDK 17+
- Maven 3.8+
- A running MySQL server (8.x recommended)

## Setup

1. **Configure credentials** (kept out of source control):
   ```bash
   cp src/main/resources/config.properties.example src/main/resources/config.properties
   # then edit config.properties with your MySQL username/password
   ```
   You can also set `MEDIXCARE_DB_USER`, `MEDIXCARE_DB_PASSWORD`,
   `MEDIXCARE_GUI_DB_URL`, `MEDIXCARE_CLI_DB_URL` as environment variables
   instead — see `DatabaseConfig.java` for the exact precedence.

2. **Create the databases.** The GUI app creates its own tables automatically
   on first run (`CREATE TABLE IF NOT EXISTS ...`). The CLI app expects its
   schema to already exist, so run it manually:
   ```bash
   mysql -u root -p < src/main/resources/schema-cli.sql
   ```
   (`schema-cli.sql` is reverse-engineered from the CLI app's queries — see
   the comment at the top of that file — so double-check it against your
   actual data before relying on it in production.)

3. **Build:**
   ```bash
   mvn clean compile
   ```

## Running

**GUI app:**
```bash
mvn javafx:run
```

**CLI app:**
```bash
mvn exec:java
```

## Notes on this refactor

The original two files hardcoded MySQL credentials directly in source
(`private static final String DB_PASSWORD = "..."`). This version moves all
connection settings into `DatabaseConfig`, which reads from a git-ignored
`config.properties` file (falling back to environment variables, then to the
apps' original defaults), so real credentials never get committed.

## License

MIT — see [LICENSE](LICENSE).
