#!/usr/bin/env bash
# Define the following variables in this file:
# export KEY_FOR_TARGET_SERVER=<location of key for non-interactive login to target server>
export TARGET_URL="{{task_dump_md_target_host}}:{{ task_dump_md_target_path }}"
export ERROR_MAIL_RECIPIENT="{{ task_dump_md_error_mail_recipient }}"