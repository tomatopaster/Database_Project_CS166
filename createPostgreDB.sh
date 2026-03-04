#! /bin/bash
echo "creating db named ... "$USER"_project_DB"
cs166_createdb $USER'_project_DB'
cs166_db_status
