# Android WebSocket Szerver (Kotlin)

Minimális projekt, ami futtat egy WebSocket szervert a telefonon és egy WebView dashboardot jelenít meg.

## Használat

1. Nyisd meg a mappát Android Studio-ban (File → Open).
2. Várj, amíg a Gradle szinkronizál.
3. Csatlakoztass egy eszközt vagy indíts emulátort.
4. Futtasd (Run).

Az app indításkor:
- Elindít egy Foreground Service-t (értesítés jelenik meg).
- A WebSocket szerver a **8765**-ös porton hallgat (`ws://127.0.0.1:8765` vagy a telefon IP-jén).
- A WebView betölti a helyi `index.html`-t, ami csatlakozik a szerverhez.

## Fontos

- Engedélyezd az értesítéseket, ha kéri.
- A szerver a telefonon fut. Más eszközről a telefon IP-címén éred el (pl. `ws://192.168.1.x:8765`).
- Böngésző-kiterjesztés Androidon nem ugyanúgy működik; az adatot más appból vagy ugyanerről a WebView-ból küldheted teszteléshez.

## Függőségek

A `app/build.gradle` már tartalmazza a `Java-WebSocket` könyvtárat.
