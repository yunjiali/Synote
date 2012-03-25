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