tmux based workflow

```sh
# tmux installation https://linuxize.com/post/getting-started-with-tmux/
tmux
# Ctrl-b % to split into two panes
# Ctrl-b o to switch to original pane

# run the server
./gradlew clean run

# Ctrl-b o to switch to the second pane
./gradlew HelloClient

# Ctrl-b o to switch to original pane
# Ctrl-c to kill the server

# Ctrl-b x to kill panes and tmux session
```

lint
```sh
# buf cli installation: https://docs.buf.build/installation
buf lint src/main/proto --config src/main/proto/buf.yaml
```
