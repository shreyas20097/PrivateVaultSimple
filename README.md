# PrivateVaultSimple

A simple desktop vault for storing private records with **SHA-256 integrity checking**.
Built with Java, Swing (GUI), JDBC and SQLite. Every record is stored with a hash of its
content; a **Verify All** action recalculates the hash and reports **VALID** (green) or
**TAMPERED** (red), so any modification made directly to the database is detected.

UI: login/register windows are small fixed-size dialogs; the vault and update windows are
colored and resizable (layout managers only, no extra libraries).

---

## 1. What you need

- **JDK 8 or newer** (Java 17 recommended) with `java` and `javac` on your PATH
- **Git** (to clone the repo)
- No database server needed — SQLite is embedded in `lib/sqlite-jdbc.jar`, already included
- Optional, only for the tamper demo in step 6: a way to edit the `.db` file
  - macOS/Linux: the `sqlite3` command-line tool
  - Windows: [DB Browser for SQLite](https://sqlitebrowser.org/) (easiest)

---

## 2. Install prerequisites

### Windows 10 / 11

1. **Install Java (JDK 17):**
   - Easiest: download the MSI installer from [adoptium.net](https://adoptium.net/) (Eclipse Temurin 17, Windows x64, JDK).
   - During setup, tick **Set JAVA_HOME variable** and **Add to PATH** (sometimes labelled *JavaSoft (Oracle) registry keys* — leave defaults otherwise).
   - Alternative with [winget](https://learn.microsoft.com/windows/package-manager/) (already on Windows 11, installable on 10):
     ```powershell
     winget install EclipseAdoptium.Temurin.17.JDK
     ```
   - Close and reopen your terminal afterwards, then check:
     ```bat
     java -version
     javac -version
     ```
     Both should print a version. If `javac` says *"not recognized"*, you opened the terminal before installing — open a new one. If it still fails, reinstall and make sure the PATH option was ticked.
2. **Install Git:**
   - Download from [git-scm.com](https://git-scm.com/download/win) and install with defaults (this also gives you **Git Bash**, which can run most `bash` commands).
   - Or: `winget install Git.Git`
   - Check in a new terminal:
     ```bat
     git --version
     ```
3. **Optional (tamper demo):** install [DB Browser for SQLite](https://sqlitebrowser.org/dl/) — download the Windows installer, open `vault_simple.db` with it later.
   - Or: `winget install DBBrowserForSQLite.DBBrowserForSQLite`

### macOS (Intel and Apple Silicon)

1. **Install Homebrew** (skip if you already have `brew`). Open **Terminal** and run:
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
2. **Install Java, Git, sqlite3:**
   ```bash
   brew install openjdk@17 git sqlite
   ```
3. **Put Java on your PATH** (macOS does not do this automatically for Homebrew JDKs):
   ```bash
   echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zprofile
   source ~/.zprofile
   ```
   - On older Intel Macs the path is `/usr/local/opt/openjdk@17/bin` instead of `/opt/homebrew/opt/openjdk@17/bin`. If unsure, run `brew --prefix` and adjust.
4. **Check everything:**
   ```bash
   java -version
   javac -version
   git --version
   sqlite3 --version
   ```
   All four should print versions without errors.

### Linux (Debian / Ubuntu)

```bash
sudo apt update
sudo apt install -y default-jdk git sqlite3
java -version && javac -version && git --version && sqlite3 --version
```
Fedora/RHEL: `sudo dnf install -y java-17-openjdk-devel git sqlite` instead.

---

## 3. Clone the repository

This repository is **private**, so you need access plus a way to authenticate.

**SSH (recommended, if your SSH key is linked to GitHub):**
```bash
git clone git@github.com:shreyas20097/PrivateVaultSimple.git
cd PrivateVaultSimple
```

**HTTPS:**
```bash
git clone https://github.com/shreyas20097/PrivateVaultSimple.git
cd PrivateVaultSimple
```
When prompted: username = your GitHub username, password = a **Personal Access Token**, *not*
your GitHub account password (GitHub disabled password auth for git operations).
Create a token at: GitHub → Settings → Developer settings → Personal access tokens → *Fine-grained
tokens* → grant **Contents: Read-only**.

> Windows tip: if the terminal asks for credentials every time, use **Git Bash** from the
> Git install above, or clone via [GitHub Desktop](https://desktop.github.com/) and open the
> folder in a terminal afterwards.

---

## 4. Build and run

> Run all commands **from inside the `PrivateVaultSimple` folder** (the database file
> `vault_simple.db` is created in whichever folder you run from).

### macOS / Linux

```bash
cd PrivateVaultSimple

mkdir -p bin
javac -cp lib/sqlite-jdbc.jar -d bin src/*.java
java -cp bin:lib/sqlite-jdbc.jar Main
```

Or just use the included script (macOS/Linux only):
```bash
./run-simple.sh
```
(if you get a permission error: `chmod +x run-simple.sh`)

### Windows — Command Prompt (cmd)

```bat
cd PrivateVaultSimple

mkdir bin
dir /s /b src\*.java > sources.txt
javac -cp "lib/sqlite-jdbc.jar" -d bin @sources.txt
del sources.txt

java -cp "bin;lib/sqlite-jdbc.jar" Main
```

### Windows — PowerShell

```powershell
cd PrivateVaultSimple

New-Item -ItemType Directory -Force bin
javac -cp "lib/sqlite-jdbc.jar" -d bin (Get-ChildItem -Recurse src\*.java).FullName
java -cp "bin;lib/sqlite-jdbc.jar" Main
```

> **Two things to get right on Windows:**
> 1. The classpath separator is `;` (semicolon), not `:`
> 2. `src\*.java` does **not** expand automatically in cmd/PowerShell — that's why the commands
>    above build a file list first (`sources.txt` / `Get-ChildItem`)

### If it does not start

- `java: command not found` / `'javac' is not recognized` → JDK not installed or terminal opened before install. Redo step 2, open a **new** terminal.
- `error: file not found: src\*.java` → you are not inside the `PrivateVaultSimple` folder. `cd` there first.
- `database is locked` → close any other copy of the app or DB Browser holding `vault_simple.db`.
- Blank window / tiny text on high-DPI Windows → update the JDK (Temurin 17+) and set display scaling to 100% while testing.

---

## 5. Optional: open in NetBeans IDE

1. **File → New Project → Java with Existing Sources**
2. **Code Assistance / Project Folder:** select the cloned `PrivateVaultSimple` folder
3. **Add Source Packages:** select the `src` folder → **Next** → **Finish**
4. Right-click the project → **Properties → Libraries** → **Add JAR/Folder** → select
   `lib/sqlite-jdbc.jar`
5. Right-click the project → **Properties → Run** → set **Main Class** to `Main` → OK
6. Press **Run** (▶) or `Shift+F6`

---

## 6. First launch

- On the very first run the app prints a fallback login:
  **username `admin`, password `admin123`**
- The **Register** button creates your own account. The first account you register deletes
  the fallback `admin` automatically, so there is no backdoor left behind.
- The confirmation popup shows `Account '<name>' created! Please login.` Every time. The
  extra `Fallback admin removed for safety.` line appears only for that very first account.
- Use **Log Out** (red button in the vault window) to return to the login screen.

---

## 7. Quick demo (tamper detection)

1. Login → **Save Record** (e.g. title `My Certificate`, content `Certificate Number: ABC123`)
2. **Verify All** → shows **VALID** in green (stored hash equals calculated hash)
3. Close the app and modify the database **without** using the app:
   - macOS/Linux: `sqlite3 vault_simple.db "UPDATE vault_records SET content='HACKED';"`
   - Windows: download [DB Browser for SQLite](https://sqlitebrowser.org/), open `vault_simple.db`,
     edit the `content` cell, save
4. Start the app again → login → **Verify All** → that record now shows **TAMPERED** in red

---

## 8. Notes

- The database `vault_simple.db` is created automatically in the folder you run the app from.
  Delete it to reset everything to a blank vault.
- `vault_simple.db` and `bin/` are excluded from git on purpose (`.gitignore`) — the database
  holds your private data, and `bin/` is generated by compiling.

---

## Project layout

```
PrivateVaultSimple/
├── src/
│   ├── Main.java              entry point: connect DB, open login window
│   ├── LoginFrame.java        login window (fixed-size, non-resizable dialog)
│   ├── RegisterFrame.java     registration window (fixed-size, non-resizable dialog)
│   ├── MainFrame.java         vault window (resizable: save / view all / verify / update / log out)
│   ├── UpdateFrame.java       update-record window (resizable: title stays one line, content stretches)
│   ├── Theme.java             shared colors + tiny button/status/text helpers
│   ├── DatabaseManager.java   JDBC connection, creates tables, seeds fallback admin
│   ├── UserManager.java       register + login
│   ├── RecordManager.java     add / get / update / list / verify records
│   ├── Record.java            record data holder
│   ├── User.java              user data holder
│   └── HashUtil.java          SHA-256 helper
├── lib/sqlite-jdbc.jar        SQLite JDBC driver (no server required)
├── run-simple.sh              build + run helper (Linux/macOS)
└── .gitignore
```