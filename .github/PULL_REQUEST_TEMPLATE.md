fixes EASY-

#### When applied it will
* 
* 
* 

#### Where should the reviewer @DANS-KNAW/easy start?

#### How should this be manually tested?

* build the project (online tests are ignored when the credentials are not specified)

      mvn clean install -Ddatacite.user=... -Ddatacite.password=... -f ~/git/service/easy/easy-app/

* in easy-dtap run `./deploy-role.sh easy_...`
* 
* 
* 

#### related pull requests on github
repo                       | PR                | Note
-------------------------- | ----------------- | ----
easy-                      | [PR#](PRlink)     |
