package redis.embedded;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import redis.embedded.util.Architecture;
import redis.embedded.util.JarUtil;
import redis.embedded.util.OS;
import redis.embedded.util.OsArchitecture;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RedisExecProvider {
    
    private final Map<OsArchitecture, String> executables = Maps.newHashMap();

    public static RedisExecProvider defaultProvider() {
        return new RedisExecProvider();
    }
    
    private RedisExecProvider() {
        initExecutables();
    }

    private void initExecutables() {
        executables.put(OsArchitecture.UNIX_x86_64, "redis-server-6.0.5");
        executables.put(OsArchitecture.MAC_OS_X_x86_64, "redis-server-6.0.5.app");
    }

    public RedisExecProvider override(OS os, String executable) {
        Preconditions.checkNotNull(executable);
        for (Architecture arch : Architecture.values()) {
            override(os, arch, executable);
        }
        return this;
    }

    public RedisExecProvider override(OS os, Architecture arch, String executable) {
        Preconditions.checkNotNull(executable);
        executables.put(new OsArchitecture(os, arch), executable);
        return this;
    }
    
    public File get() throws IOException {
        String executablePath = "";
        if (System.getProperty("os.name").contains("Mac")) {
            executablePath = executables.get(OsArchitecture.MAC_OS_X_x86_64);
        } else {
            executablePath = executables.get(OsArchitecture.UNIX_x86_64);
        }
        return fileExists(executablePath) ?
                new File(executablePath) :
                JarUtil.extractExecutableFromJar(executablePath);

    }

    private boolean fileExists(String executablePath) {
        return new File(executablePath).exists();
    }
}
