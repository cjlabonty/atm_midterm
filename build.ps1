# Clean the project and install dependencies
mvn clean install

# Run the unit tests
mvn test

# Run style check
mvn checkstyle:check

# Build documentation
mvn javadoc:javadoc

mvn site
