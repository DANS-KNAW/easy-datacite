Execution instructions
======================

Changing the access rights of datasets with Synthegra reports requires several steps in a specific order. For variations in the instruction, follow the links to the github readme's or try the --help option of a command.

* Prepare with
  * `./deploy-role task_change_synthegra_to_cc0`
  * `./deploy-role task_add_new_license`
  * the first deploys an executable and is required just once or perhaps a variation for future similar requests, the latter should be added to `easy-app/provisioning/sites.yml` but _currently sets only an environment variable_
* Get `input.csv` with the datasets to change
  * on production: download the [csv](https://media-api.atlassian.io/file/b23bdf31-fc96-40f1-9a6e-4768c69e44c5/binary?token=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIzNzRjNzA3Zi0yZWIzLTRmMTYtYmE0Ny04OTYwYmVjZDJlNmQiLCJhY2Nlc3MiOnsidXJuOmZpbGVzdG9yZTpmaWxlOmIyM2JkZjMxLWZjOTYtNDBmMS05YTZlLTQ3NjhjNjllNDRjNSI6WyJyZWFkIl19LCJleHAiOjE0ODA0MTE0MTMsIm5iZiI6MTQ4MDQxMDc1M30.Cdhyul8zSD6Xr-8eG85m-yAQ4jzG5DfoFjYkKnKTuPA&client=374c707f-2eb3-4f16-ba47-8960becd2e6d&name=Synthegra+omzetten+CC0.csv)
  * for deasy:
   
        cp easy-app/tool/task-change-synthegra-to-cc0/src/test/resources/deasy-data.csv easy-dtap/shared/input.csv
   
    what makes the file available at `/vagrant/shared/input.csv`
* On deasy you will need to execute `sudo su` before executing the following commands
* **NOTE**: add the command line option `--doUpdate` to execute the commands for real (except for `easy-update-fs-rdb` and `easy-update-solr-index`)
* Change the file rights:
  * The next command prompts for credentials: `egrep '(fedora.admin|ldap.sec)' /opt/easy-webui/cfg/application.properties` 
  * `/home/easy-tool/task-change-synthegra-to-cc0/bin/run.sh input.csv`
* The previous command creates the files `changed_pids.txt` and `changed_files.txt` on `/home/easy-tool/task-change-synthegra-to-cc0/`
  * copy the dataset IDs to `new-rights.csv` and add a second column with `OPEN_ACCESS`
  * provide a header line which will be ignored and is supposed to contain something like `fedoraID, newValue` 
* [Change](https://github.com/DANS-KNAW/easy-update-metadata-dataset#readme) the dataset rights:
  * `easy-update-metadata-dataset -s EMD -t accessRights new-rights.csv`
  * `easy-update-metadata-dataset -s DC -t rights new-rights.csv`
* [Update](https://github.com/DANS-KNAW/easy-app/blob/c28b3e6556cea014650f8a9fdeacbbc2a6df23fc/tool/task-add-new-license#readme) the licenses _but do not mail them_:
  * `/home/easy-tool/task-add-new-license/bin/task-add-new-license --pids-file changed_pids.txt --output-file new-license-added.txt`
* [Update](https://github.com/DANS-KNAW/easy-update-fs-rdb#readme) the file index:
  * `easy-update-fs-rdb --file changed_pids.txt`
* [Update](https://github.com/DANS-KNAW/easy-update-solr-index#readme) the solr index: 
  * `easy-update-solr-index changed_pids.txt`
* Update the OAI-PMH cache
  * ...
* An archivist can manually send a new example license document.

In order to show the results in the webui, the hibernate cache needs to be cleared. This can be done by restarting tomcat.
