create table user
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	user_name varchar(255) not null unique,
	password varchar(255) not null,
	first_name varchar(255) not null,
	last_name varchar(255) not null,
	email varchar(255) not null,
	role int(11) not null
) engine = innodb default charset = utf8;

create table user_group
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	owner_id bigint(20) not null,
	name varchar(255) not null,
	shared bit(1) not null,
	unique (owner_id, name),
	foreign key (owner_id) references user (id) on delete cascade
) engine = innodb default charset = utf8;

create table user_group_member
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	group_id bigint(20) not null,
	user_id bigint(20) not null,
	unique (group_id, user_id),
	foreign key (group_id) references user_group (id) on delete cascade,
	foreign key (user_id) references user (id) on delete cascade
) engine = innodb default charset = utf8;

create table resource
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	class varchar(255) not null,
	owner_id bigint(20) not null,
	title varchar(255),
	perm int(11),
	url varchar(255),
	content text,
	parent_synmark_id bigint(20),
	synmark_note_id bigint(20),
	synmark_next_id bigint(20),
	foreign key (owner_id) references user (id) on delete cascade,
	foreign key (parent_synmark_id) references resource (id) on delete cascade,
	foreign key (synmark_note_id) references resource (id) on delete set null,
	foreign key (synmark_next_id) references resource (id) on delete set null
) engine = innodb default charset = utf8;

create table presentation_slide
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	presentation_id bigint(20) not null,
	slide_index int(11) not null,
	url varchar(255) not null,
	foreign key (presentation_id) references resource (id) on delete cascade
) engine = innodb default charset = utf8;

create table annotation
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	class varchar(255) not null,
	owner_id bigint(20) not null,
	perm int(11),
	source_resource_id bigint(20) not null,
	target_resource_id bigint(20),
	target_annotation_id bigint(20),
	foreign key (owner_id) references user (id) on delete cascade,
	foreign key (source_resource_id) references resource (id) on delete cascade,
	foreign key (target_resource_id) references resource (id) on delete cascade,
	foreign key (target_annotation_id) references annotation (id) on delete cascade
) engine = innodb default charset = utf8;

create table synpoint
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	annotation_id bigint(20) not null,
	source_start int(11),
	source_end int(11),
	target_start int(11),
	target_end int(11),
	foreign key (annotation_id) references annotation (id) on delete cascade
) engine = innodb default charset = utf8;

create table permission
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	class varchar(255) not null,	
	group_id bigint(20) not null,  
	perm int(11) not null,
	resource_id bigint(20),
	annotation_id bigint(20),
	foreign key (group_id) references user_group (id) on delete cascade,
	foreign key (resource_id) references resource (id) on delete cascade,
	foreign key (annotation_id) references annotation (id) on delete cascade
) engine = innodb default charset = utf8;

create table user_profile
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	owner_id bigint(20) not null,
	name varchar(255) not null,
	default_profile bit(1) not null,
	unique (owner_id, name),
	foreign key (owner_id) references user (id) on delete cascade
) engine = innodb default charset = utf8;

create table profile_entry
(
	id bigint(20) primary key auto_increment,
	version bigint(20) not null,
	profile_id bigint(20) not null,
	resource_id bigint(20),	
	name varchar(255) not null,
	value varchar(255) not null,
	unique (profile_id, resource_id, name),
	foreign key (profile_id) references user_profile (id) on delete cascade,
	foreign key (resource_id) references resource (id) on delete cascade
) engine = innodb default charset = utf8;

create table `ibmtrans_job` (
  `id` bigint(20) primary key auto_increment,
  `version` bigint(20) not null,
  `create_date` datetime not null,
  `edit_url` varchar(255) not null,
  `job_id` varchar(255) not null,
  `owner_id` bigint(20) not null,
  `resource_id` bigint(20) not null,
  `saved` bit(1) not null,
  `status` int(11) not null,
  `title` varchar(255) not null,
  `transcript` longtext default null,
  `url` varchar(255) not null,
  `vsxml_transcript` longtext default null,
  `xml_transcript` longtext default null,
  foreign key (`owner_id`) references `user` (`id`) on delete cascade,
  foreign key (`resource_id`) references `resource` (`id`) on delete cascade
) engine=innodb default charset=utf8;
