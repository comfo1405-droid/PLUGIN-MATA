# messhy_js — All-in-one Paper plugin (MC 1.21.11)

## What's included
- **Login/Register** — `/register <password> <confirmPassword>`, `/login <password>`. Players are frozen (can't move/take damage/chat/run other commands) until logged in, and are kicked if they don't log in within 60s. Data is saved to `plugins/messhy_js/user_info.json` (passwords are salted + SHA-256 hashed, never stored in plain text).
- **Server privacy** — `/server private` or `/server public`. Private mode defaults to **1** registration slot per IP; public defaults to 3.
- **`/reg-slots add|remove <user> <slots>`** — grant/revoke extra registration slots for a user's IP, saved in `user_info.json`.
- **`/announcement <message> <color>`** — broadcast a boxed, colored message to everyone.
- **`/user-say <user> <message> <color>`** — send a private colored message to one user.
- **`/plugins`** — this is Bukkit's built-in command; since the plugin is named `messhy_js` in `plugin.yml`, it will show up there automatically.
- **`/anti-spam enable|disable`** — kicks players who send messages too fast or repeat the same message.
- **`/bad-word add <word>`** — flagged words trigger a warning; 3 warnings = auto-kick. Warning counts via `/warnings <user>`.
- **`/anti-cheating enable|disable`** — lightweight heuristic that flags sustained unauthorized flight. This is a starting point, not a replacement for a dedicated anti-cheat like Matrix/Grim — packet-level detection is a much bigger project.
- **`/anti-auto-totem enable|disable`** — detects rapid offhand totem swapping / rapid totem resurrection patterns typical of autototem macros, and issues a real 7-day ban (via the server's ban list) on trigger. `/unban <user>` reverses it.
- Bonus dev tools: `/freeze <user>`, `/vanish`, `/warnings <user>`, `/unban <user>`.

All admin commands require permission `messhy.admin` (granted to server operators by default).

## Project layout
Standard Maven layout, targeting Java 21 / Paper API `1.21.11-R0.1-SNAPSHOT`:
```
pom.xml
src/main/java/com/messhy/plugin/...
src/main/resources/plugin.yml
.github/workflows/build.yml   <- builds the jar automatically on GitHub
```

## Building the .jar with GitHub (no local Maven needed)
Since you don't have a local terminal/Maven, let GitHub Actions build it for you:

1. Create a new **public or private repo** on GitHub (e.g. `messhy-js`).
2. Upload every file from this project into the repo, keeping the folder structure exactly as-is (including the hidden `.github` folder — GitHub's web uploader preserves it if you drag the whole extracted folder in, or use "Add file → Upload files" and drag the `.github/workflows/build.yml` in separately if it gets skipped).
3. Once pushed to the `main` branch, go to the repo's **Actions** tab. A workflow called "Build Plugin" will run automatically.
4. When it finishes (green checkmark), open that run and scroll to **Artifacts** — download `messhy-js-plugin.zip`. Inside is `messhy-js-1.0.0.jar`.
5. Upload that `.jar` to your Pterodactyl-style panel's `plugins/` folder and restart the server.

### Optional: get a jar attached to a GitHub Release automatically
Push a version tag and the workflow will also attach the jar to a Release:
```
git tag v1.0.0
git push origin v1.0.0
```
(If you're doing this from GitHub's web UI instead of git commands, you can create a tag/release directly from the repo's "Releases" page — the workflow re-runs and attaches the jar.)

## First run
1. Drop the jar in `plugins/`, restart.
2. It creates `plugins/messhy_js/user_info.json` automatically.
3. Set yourself as OP if you aren't already, then try `/server private`, `/bad-word add badword`, `/anti-spam enable`, etc.

## Extending it
Everything is split into small, single-purpose classes under `commands/`, `listeners/`, and `data/` — new commands/toggles follow the same pattern as `ToggleCommands.java`, so it's straightforward to bolt on more systems later (economy, tab list, join messages, etc.) if you want to keep growing it.
