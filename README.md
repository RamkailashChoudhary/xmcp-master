3# xmcp — X MCP demos with Spring AI

Spring Boot app (Spring Boot 4.1, Spring AI 2.0) that connects to two of
X's MCP servers and lets Claude call their tools. Two demos, side by side:

| Demo | What it does | Try it |
|------|--------------|--------|
| **1 — Docs** | Answers questions from the X API docs (public MCP server, no login) | `GET /docs` |
| **2 — Live X** | Searches real posts on X (live X API) | `GET /search?topic=...` |

Both talk to X over the same transport (streamable HTTP) and share one
`ChatClient`. Demo 2 just adds an auth header — no bridge, no Node, no browser
login. (When *would* you need the bridge? See the bottom of this file.)

## What you need

- **Java 21**
- **`ANTHROPIC_API_KEY`** — the Claude model the app talks to.
- **`X_BEARER_TOKEN`** — your app's *App-only Bearer token* from the
  [X Developer Portal](https://developer.x.com) (your app → *Keys and tokens*).
  Required, because Demo 2 is turned on by default.

## Run it

```bash
export ANTHROPIC_API_KEY=...
export X_BEARER_TOKEN=...
./mvnw spring-boot:run
```

Then hit either endpoint:

```bash
curl localhost:8080/docs
curl 'localhost:8080/search?topic=Spring%20Boot%204.1'
```

> Running in IntelliJ? Put both keys in **Run → Edit Configurations →
> Environment variables** — a shell `export` doesn't reach IntelliJ.

## How Demo 2 signs its requests

The live X API needs an `Authorization: Bearer <token>` header. Spring AI 2.0 has
no setting for per-connection headers, so `XApiAuthConfig` adds one: it attaches
the token to any request going to `api.x.com` and leaves the `docs.x.com`
connection alone.

The token is read from `X_BEARER_TOKEN` — an environment variable, or a line in
`application.yaml`. **It's a secret, so don't commit a real one.**

## Do I need the xurl bridge?

X's docs mention a local `xurl` bridge (`npx @xdevplatform/xurl mcp ...`).
**This app doesn't use it**, and here's the rule:

- **Reading** (like this search demo) → an app-only Bearer token is enough. No bridge.
- **Acting as your own account** — posting, bookmarking, anything that writes →
  you need an OAuth login. `api.x.com/mcp` can't run that login on its own, so the
  `xurl` bridge does it for you and injects the token.

In short: **read → Bearer token. Write → xurl bridge.**

## Good to know

- **No results?** Recent search needs a **Basic+** X tier; full-archive needs **Pro**.
- **App won't start, or a 401?** The token is missing, wrong, or expired. Because
  Demo 2 is on by default, a bad token stops the whole app from starting. (To run
  only Demo 1, comment out the `x-api` connection in `application.yaml`.)
- Watch the log for `Executing tool call: <name>` to see the model call an MCP tool.
