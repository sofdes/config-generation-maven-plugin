# From: ${filter.source}

# Environment substitution can be used for Continuous Integration scripts
SCRIPT_NAME=$(dirname $0)

# Stop application server
CMD=${app.server.bin.dir}/stop.sh
echo "$CMD"
$CMD

# Copy web app config to install directory
CMD="cp $SCRIPT_NAME/properties/unit_test_db_connection.properties ${install.dir}/db_connection.properties"
echo "$CMD"
$CMD

CMD="cp $SCRIPT_NAME/properties/unit_test_other_config.properties ${install.dir}/unit_test_other_config.properties"
echo "$CMD"
$CMD

# Run Liquibase Changelog with properties file for the current environment
CMD="${install.dir}/liquibase/update_db.sh $SCRIPT_NAME/liquibase/liquibase.properties"
echo "$CMD"
$CMD

# Restart application server
CMD=${app.server.bin.dir}/start.sh
echo "$CMD"
$CMD

echo "Auto deployment completed - check here: ${verify.service.url}"
