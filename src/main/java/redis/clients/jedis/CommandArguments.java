package redis.clients.jedis;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.args.RawableFactory;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.params.IParams;

public class CommandArguments implements Iterable<Rawable> {

  private final ArrayList<Rawable> args;

  private boolean blocking;

  private final String keyPrefix;

  private CommandArguments() {
    throw new InstantiationError();
  }

  public CommandArguments(ProtocolCommand command) {
    args = new ArrayList<>();
    args.add(command);
    this.keyPrefix = null;
  }

  public CommandArguments(ProtocolCommand command, String keyPrefix) {
    args = new ArrayList<>();
    args.add(command);
    this.keyPrefix = keyPrefix;
  }

  public ProtocolCommand getCommand() {
    return (ProtocolCommand) args.get(0);
  }

  public CommandArguments add(Object arg) {
    if (arg == null) {
      throw new IllegalArgumentException("null is not a valid argument.");
    } else if (arg instanceof Rawable) {
      args.add((Rawable) arg);
    } else if (arg instanceof byte[]) {
      args.add(RawableFactory.from((byte[]) arg));
    } else if (arg instanceof String) {
      args.add(RawableFactory.from((String) arg));
    } else if (arg instanceof Boolean) {
      args.add(RawableFactory.from(Integer.toString((Boolean) arg ? 1 : 0)));
    } else {
      args.add(RawableFactory.from(String.valueOf(arg)));
    }
    return this;
  }

  public CommandArguments addObjects(Object... args) {
    for (Object arg : args) {
      add(arg);
    }
    return this;
  }

  public CommandArguments addObjects(Collection args) {
    args.forEach(arg -> add(arg));
    return this;
  }

  public CommandArguments key(Object key) {
    if (keyPrefix == null) {
      key0(key);
      return this;
    }
    if (key instanceof Rawable) {
      Rawable raw = (Rawable) key;
      processKey(raw.getRaw());
      args.add(raw);
    } else if (key instanceof byte[]) {
      byte[] raw = new byte[((byte[]) key).length + keyPrefix.length()];
      System.arraycopy(keyPrefix.getBytes(StandardCharsets.UTF_8), 0, raw, 0, keyPrefix.length());
      System.arraycopy((byte[]) key, 0, raw, keyPrefix.length(), ((byte[]) key).length);
      processKey(raw);
      args.add(RawableFactory.from(raw));
    } else if (key instanceof String) {
      String raw = keyPrefix + (String) key;
      processKey(raw);
      args.add(RawableFactory.from(raw));
    } else {
      throw new IllegalArgumentException("\"" + key.toString() + "\" is not a valid argument.");
    }
    return this;
  }

  public CommandArguments key0(Object key) {
    if (key instanceof Rawable) {
      Rawable raw = (Rawable) key;
      processKey(raw.getRaw());
      args.add(raw);
    } else if (key instanceof byte[]) {
      byte[] arr = new byte[((byte[]) key).length + keyPrefix.length()];
      System.arraycopy(keyPrefix.getBytes(StandardCharsets.UTF_8), 0, arr, 0, keyPrefix.length());
      System.arraycopy((byte[]) key, 0, arr, keyPrefix.length(), ((byte[]) key).length);
      byte[] raw = keyPrefix.getBytes(StandardCharsets.UTF_8);
      processKey(raw);
      args.add(RawableFactory.from(raw));
    } else if (key instanceof String) {
      String raw = keyPrefix + (String) key;
      processKey(raw);
      args.add(RawableFactory.from(raw));
    } else {
      throw new IllegalArgumentException("\"" + key.toString() + "\" is not a valid argument.");
    }
    return this;
  }

  public final CommandArguments keys(Object... keys) {
    for (Object key : keys) {
      key(key);
    }
    return this;
  }

  public final CommandArguments addParams(IParams params) {
    params.addParams(this);
    return this;
  }

  protected CommandArguments processKey(byte[] key) {
    // do nothing
    return this;
  }

  protected final CommandArguments processKeys(byte[]... keys) {
    for (byte[] key : keys) {
      processKey(key);
    }
    return this;
  }

  protected CommandArguments processKey(String key) {
    // do nothing
    return this;
  }

  protected final CommandArguments processKeys(String... keys) {
    for (String key : keys) {
      processKey(key);
    }
    return this;
  }

  public int size() {
    return args.size();
  }

  @Override
  public Iterator<Rawable> iterator() {
    return args.iterator();
  }

  public boolean isBlocking() {
    return blocking;
  }

  public CommandArguments blocking() {
    this.blocking = true;
    return this;
  }
}
