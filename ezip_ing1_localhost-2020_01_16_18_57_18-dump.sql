--
-- Name: students; Type: TABLE; Schema: ezip_ing1
--

CREATE TABLE students (
	id int(20) NOT NULL AUTO_INCREMENT,
    name varchar(64) NOT NULL,
    firstname varchar(64) NOT NULL,
    groupname varchar(8) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO students (name, firstname, groupname) VALUES ("ANONYMOUS", "Stephane", "Admin");

CREATE TABLE asset_dependencies(
   asset_dependency_id VARCHAR(50),
   parent_asset VARCHAR(16) NOT NULL,
   child_asset VARCHAR(16) NOT NULL,
   dependency_type VARCHAR(16) NOT NULL,
   PRIMARY KEY(asset_dependency_id)
);

CREATE TABLE render_configs(
   config_id VARCHAR(16),
   project_id VARCHAR(16) NOT NULL,
   name VARCHAR(255) NOT NULL,
   config_data TEXT,
   created_at DATE,
   is_default boolean,
   PRIMARY KEY(config_id)
);

CREATE TABLE assets(
   asset_id VARCHAR(16),
   project_id VARCHAR(16) NOT NULL,
   asset_type CHAR(25) NOT NULL,
   file_path VARCHAR(50) NOT NULL,
   checksum VARBINARY(50) NOT NULL,
   uploaded_at DATE,
   metadata TEXT,
   PRIMARY KEY(asset_id)
);

CREATE TABLE extensions(
   extension_id VARCHAR(16),
   name VARCHAR(255) NOT NULL,
   version VARCHAR(32) NOT NULL,
   author VARCHAR(100),
   entry_point VARCHAR(500) NOT NULL,
   is_enabled boolean,
   installed_at DATE,
   metadata TEXT,
   description TEXT,
   PRIMARY KEY(extension_id)
);

CREATE TABLE themes(
   theme_id VARCHAR(50),
   name VARCHAR(255) NOT NULL,
   author VARCHAR(255),
   is_default boolean,
   styles TEXT,
   created_at DATE,
   PRIMARY KEY(theme_id)
);

CREATE TABLE logs(
   log_id VARCHAR(50),
   type VARCHAR(50) NOT NULL,
   message TEXT NOT NULL,
   context TEXT,
   created_at DATE,
   PRIMARY KEY(log_id)
);

CREATE TABLE projects(
   project_id VARCHAR(50),
   user_id VARCHAR(50) NOT NULL,
   name VARCHAR(100),
   description TEXT,
   created_at DATE,
   updated_at DATE,
   scene_data TEXT,
   metadata TEXT,
   asset_id VARCHAR(16) NOT NULL,
   config_id VARCHAR(16) NOT NULL,
   extension_id VARCHAR(16) NOT NULL,
   PRIMARY KEY(project_id),
   FOREIGN KEY(asset_id) REFERENCES assets(asset_id),
   FOREIGN KEY(config_id) REFERENCES render_configs(config_id),
   FOREIGN KEY(extension_id) REFERENCES extensions(extension_id)
);

CREATE TABLE users(
   user_id INT,
   username VARCHAR(64) NOT NULL,
   email VARCHAR(255) NOT NULL,
   created_at DATE,
   last_login DATE,
   is_active boolean,
   preferences TEXT,
   password_hash CHAR(97) NOT NULL,
   extension_id VARCHAR(16) NOT NULL,
   theme_id VARCHAR(50) NOT NULL,
   log_id VARCHAR(50) NOT NULL,
   project_id VARCHAR(50) NOT NULL,
   PRIMARY KEY(user_id),
   UNIQUE(username),
   UNIQUE(email),
   FOREIGN KEY(extension_id) REFERENCES extensions(extension_id),
   FOREIGN KEY(theme_id) REFERENCES themes(theme_id),
   FOREIGN KEY(log_id) REFERENCES logs(log_id),
   FOREIGN KEY(project_id) REFERENCES projects(project_id)
);

CREATE TABLE depends(
   asset_dependency_id VARCHAR(50),
   asset_id VARCHAR(16),
   PRIMARY KEY(asset_dependency_id, asset_id),
   FOREIGN KEY(asset_dependency_id) REFERENCES asset_dependencies(asset_dependency_id),
   FOREIGN KEY(asset_id) REFERENCES assets(asset_id)
);


