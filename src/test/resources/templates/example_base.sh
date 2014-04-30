# Environment substitution can be used for Continuous Integration scripts

cp $0/properties/unit_test_db_connection.properties ${install.dir}/db_connection.properties
cp $0/properties/unit_test_oter_config.properties ${install.dir}/db_connection.properties
cp $0/liquibase/liquibase.properties ${install.dir}/liquibase.properties
