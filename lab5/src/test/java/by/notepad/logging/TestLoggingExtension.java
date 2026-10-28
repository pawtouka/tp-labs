package by.notepad.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public class TestLoggingExtension
        implements BeforeTestExecutionCallback, TestWatcher {

    private static final Logger LOGGER =
            LogManager.getLogger(TestLoggingExtension.class);

    private static final Level OK = Level.forName("OK", 350);
    private static final Level FAIL = Level.forName("FAIL", 150);

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        LOGGER.info("INFO | START | {}", context.getDisplayName());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        LOGGER.log(OK, "OK | PASSED | {}", context.getDisplayName());
    }

    @Override
    public void testFailed(
            ExtensionContext context,
            Throwable cause) {

        LOGGER.log(
                FAIL,
                "FAIL | FAILED | {} | Reason: {}",
                context.getDisplayName(),
                cause.getMessage()
        );

        LOGGER.error("Test failure details", cause);
    }

    @Override
    public void testAborted(
            ExtensionContext context,
            Throwable cause) {

        LOGGER.warn(
                "WARN | ABORTED | {} | Reason: {}",
                context.getDisplayName(),
                cause.getMessage()
        );
    }

    @Override
    public void testDisabled(
            ExtensionContext context,
            Optional<String> reason) {

        LOGGER.warn(
                "WARN | DISABLED | {} | Reason: {}",
                context.getDisplayName(),
                reason.orElse("No reason specified")
        );
    }
}