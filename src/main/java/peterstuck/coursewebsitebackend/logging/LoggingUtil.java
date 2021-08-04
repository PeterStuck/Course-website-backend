package peterstuck.coursewebsitebackend.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import peterstuck.coursewebsitebackend.utils.JwtUtil;

@Component
public class LoggingUtil {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(LoggingUtil.class);

    public String getUserEmailFromRequest(Object rawJWT) {
        String token = String.valueOf(rawJWT).substring(7);
        return jwtUtil.extractUsername(token);
    }

    public String convertArgsToStringWithoutJWT(Object[] args, int authHeaderIndex) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i != authHeaderIndex)
                builder.append("[").append(i).append("] ").append(args[i]).append("; ");
        }

        return builder.toString();
    }

    public Logger logger() {
        return logger;
    }

}
