# Known Issues

Problems we've hit while setting up or running the framework, and the fix that worked. Each entry has the symptom, the cause and the resolution steps.

---

## KI-001: Selenium Grid won't start on Windows, "ports are not available"

| Item | Detail |
| --- | --- |
| Area | Docker / Selenium Grid |
| OS | Windows 10 / 11 with Docker Desktop (WSL2 or Hyper-V) |
| Status | Resolved |

### Symptom

`docker compose up` fails while starting the hub:

```
Error response from daemon: ports are not available: exposing port TCP 0.0.0.0:4444 -> 127.0.0.1:0:
listen tcp 0.0.0.0:4444: bind: An attempt was made to access a socket in a way forbidden by its access permissions.
```

### Cause

Windows reserves blocks of TCP ports for Hyper-V and WSL2, and no program is allowed to use a reserved port. On this machine the range **4383 to 4482** was reserved, which covers all three default Grid ports (4442, 4443 and 4444). No other program was using the port.

### How to confirm

Run these in Command Prompt:

```
netstat -ano | findstr :4444
netsh interface ipv4 show excludedportrange protocol=tcp
```

- If `netstat` shows nothing, no program is using the port.
- If `4444` falls inside one of the listed ranges, Windows has reserved it, and this is the issue.

### Resolution

1. In `docker-compose.yml`, change the `selenium-hub` ports so only the Grid is published, on a free port:

   ```yaml
   selenium-hub:
     ports:
       - "5444:4444"
   ```

   Ports 4442 and 4443 are only used between the hub and the nodes inside Docker's network, so they don't need to be published at all.

2. In `src/main/resources/config.properties`, point the framework at the new port:

   ```
   gridUrl=http://localhost:5444
   ```

3. Restart the Grid:

   ```
   docker compose down
   docker compose up -d
   ```

4. Open **http://localhost:5444**. The Grid console should list the Chrome, Firefox and Edge nodes.

If 5444 is ever reserved on another machine, pick any port that isn't in the `excludedportrange` list and use it in both files.
