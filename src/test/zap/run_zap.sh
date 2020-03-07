#!/bin/bash
docker run --rm \
	-v $(pwd):/zap/wrk:rw \
	--network=host `#Needed as we run against localhost`\
	owasp/zap2docker-weekly\
	zap-full-scan.py \
		-j `#Include AJAX spider`\
		--hook=/zap/wrk/authentication-hooks.py `#Incluse hooks for authentication`\
		-c zap_config `#Include config file with rules`\
		-z "-config globalexcludeurl.url_list.url.regex='^https:\/\/localhost\/auth.*$'" `#Exclude the auth server as we do not want to check it`\
		-t https://localhost `#The actual target`
