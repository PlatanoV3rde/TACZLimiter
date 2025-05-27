#!/usr/bin/env sh

APP_HOME=$(dirname "$0")

if [ -n "$JAVA_HOME" ] ; then
    JAVA_EXEC="$JAVA_HOME/bin/java"
else
    JAVA_EXEC=java
fi

exec "$JAVA_EXEC" $JAVA_OPTS -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
