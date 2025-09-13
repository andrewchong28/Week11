/* 
 select * from project;
 select * from category;
 select * from material;
 select * from step;
 
 **/
 



drop table if exists project_category;  -- depends on project and category
drop table if exists material;          -- depends on project
drop table if exists step;              -- depends on project
drop table if exists project;           -- now safe to drop
drop table if exists category;          -- now safe to drop

create table project(
	project_id INT AUTO_INCREMENT NOT NULL,
	project_name VARCHAR(128) NOT NULL,
	estimated_hours DECIMAL(7,2),
	actual_hours DECIMAL(7,2),
	difficulty INT,
	notes TEXT,
	PRIMARY KEY(project_id)
);

create table material(
	material_id INT AUTO_INCREMENT NOT NULL,
	project_id INT NOT NULL,
	material_name VARCHAR(128) NOT NULL,
	num_required INT,
	cost decimal(7,2),
	step_id int,
	PRIMARY KEY (material_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

create table step(
	step_id INT AUTO_INCREMENT NOT NULL,
	project_id INT NOT NULL,
	step_text TEXT  NOT NULL,
	step_order INT  NOT NULL,	
	PRIMARY KEY (step_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

create table category(
	category_id INT AUTO_INCREMENT NOT NULL,
	category_name VARCHAR(128) NOT NULL,
	PRIMARY KEY (category_id)
);

create table project_category(
	project_id INT NOT NULL,
	category_id INT NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE,
    UNIQUE KEY(project_id, category_id)
);

INSERT INTO category (category_name) VALUES ('Doors and Windows');
INSERT INTO category (category_name) VALUES ('Landscaping');
INSERT INTO category (category_name) VALUES ('Bathrooms');
INSERT INTO category (category_name) VALUES ('Kitchen');
 
INSERT INTO project(project_name, estimated_hours, actual_hours, difficulty, notes)
values('House 1', 100.00, 80.00, 2, 'Lot 001');
INSERT INTO project(project_name, estimated_hours, actual_hours, difficulty, notes)
values('House 2', 110.00, 70.00, 2, 'Lot 002');
INSERT INTO project(project_name, estimated_hours, actual_hours, difficulty, notes)
values('House 3', 90.00, 115.00, 2, 'Lot 003');
INSERT INTO project(project_name, estimated_hours, actual_hours, difficulty, notes)
values('House 4', 90.00, 115.00, 2, 'Lot 004');


 INSERT INTO material (project_id, material_name, num_required)
 VALUES(1, '2-inch screws', 20);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(1, 'Trees', 6);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(1, 'faucets', 4);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(1, 'stove', 1);
 
INSERT INTO material (project_id, material_name, num_required)
 VALUES(2, '2-inch screws', 20);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(2, 'Trees', 6);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(2, 'faucets', 4);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(2, 'stove', 1);
 
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(3, '2-inch screws', 20);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(3, 'Trees', 6);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(3, 'faucets', 4);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(3, 'stove', 1);
 
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(4, '2-inch screws', 20);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(4, 'Trees', 6);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(4, 'faucets', 4);
 INSERT INTO material (project_id, material_name, num_required)
 VALUES(4, 'stove', 1);
 
INSERT INTO step (project_id, step_text, step_order)
VALUES(1, 'Screw door hangers on the top and bottom of each side of the door frame', 1);
INSERT INTO step (project_id, step_text, step_order)
VALUES(1, 'Plant trees', 2);
INSERT INTO step (project_id, step_text, step_order)
VALUES(1, 'Install faucets', 3);
INSERT INTO step (project_id, step_text, step_order)
VALUES(1, 'Install stove', 4);

INSERT INTO step (project_id, step_text, step_order)
VALUES(2, 'Screw door hangers on the top and bottom of each side of the door frame', 1);
INSERT INTO step (project_id, step_text, step_order)
VALUES(2, 'Plant trees', 2);
INSERT INTO step (project_id, step_text, step_order)
VALUES(2, 'Install faucets', 3);
INSERT INTO step (project_id, step_text, step_order)
VALUES(2, 'Install stove', 4);

INSERT INTO step (project_id, step_text, step_order)
VALUES(3, 'Screw door hangers on the top and bottom of each side of the door frame', 1);
INSERT INTO step (project_id, step_text, step_order)
VALUES(3, 'Plant trees', 2);
INSERT INTO step (project_id, step_text, step_order)
VALUES(3, 'Install faucets', 3);
INSERT INTO step (project_id, step_text, step_order)
VALUES(3, 'Install stove', 4);

INSERT INTO step (project_id, step_text, step_order)
VALUES(4, 'Screw door hangers on the top and bottom of each side of the door frame', 1);
INSERT INTO step (project_id, step_text, step_order)
VALUES(4, 'Plant trees', 2);
INSERT INTO step (project_id, step_text, step_order)
VALUES(4, 'Install faucets', 3);
INSERT INTO step (project_id, step_text, step_order)
VALUES(4, 'Install stove', 4);

INSERT INTO project_category (project_id, category_id)
VALUES(1, 1);
INSERT INTO project_category (project_id, category_id)
VALUES(1, 2);
INSERT INTO project_category (project_id, category_id)
VALUES(1, 3);
INSERT INTO project_category (project_id, category_id)
VALUES(1, 4);

INSERT INTO project_category (project_id, category_id)
VALUES(2, 1);
INSERT INTO project_category (project_id, category_id)
VALUES(2, 2);
INSERT INTO project_category (project_id, category_id)
VALUES(2, 3);
INSERT INTO project_category (project_id, category_id)
VALUES(2, 4);
 
INSERT INTO project_category (project_id, category_id)
VALUES(3, 1);
INSERT INTO project_category (project_id, category_id)
VALUES(3, 2);
INSERT INTO project_category (project_id, category_id)
VALUES(3, 3);
INSERT INTO project_category (project_id, category_id)
VALUES(3, 4);
 
INSERT INTO project_category (project_id, category_id)
VALUES(4, 1);
INSERT INTO project_category (project_id, category_id)
VALUES(4, 2);
INSERT INTO project_category (project_id, category_id)
VALUES(4, 3);
INSERT INTO project_category (project_id, category_id)
VALUES(4, 4);
