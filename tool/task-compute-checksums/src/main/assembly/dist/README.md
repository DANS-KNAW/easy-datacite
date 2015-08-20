# [EASY-913] Checksum Enable Task

[EASY-913]: (https://drivenbydata.atlassian.net/browse/EASY-913)

## Scripts

* `calculate.sh` pre-calculates checksums for all 'managed' streams
   logs Fedora object ids handled in done-calc-file-obj-ids.log
* `setFiles.sh` activates checksums for the pre-calculates streams,
   quits as soon verification fails for a stream to prevent spoiling
   more than a single stream.
   logs Fedora object ids handled in done-set-file-obj-ids.log
* `setOther.sh` activates checksums for streams not in `log/stream-ids.log`
  assuming a stream-id is not external for one object and embedded in FOXML
  for another object.
  logs Fedora object ids handled in done-set-other-obj-ids.log
* `verify.sh` Double checks the pre-calculated checksums with the ones
  stored by fedora.
  logs Fedora object ids handled in done-verify-obj-ids.log

## Arguments

The scripts `calculate.sh` and `setOther.sh` require one positional
argument to select the objects to process. Examples:

* `easy-f*:1???` for files and folders, both from 1000 to 1999
* `*:*` for all objects. All objects may take days and thus
  runs a serious risk of interruption preventing to
  create `log/stream-ids.log`

All script take an optional extra argument for the file with Fedora object ids that must be skipped.
When a script fails it can be re-run using the log file containing the ids that have already been processed.


Note that objects are processed in random order, so in case of
interruption there will be gaps in the processed range.

## Working directory

No variable is required to set the home directory.
The directory one level above the location of the
scripts is set as working directory by the scripts.

## Example usage

    $ time ./calculate.sh easy-*:* log/done-calc-file-obj-ids.log
    How many do we have
    $ wc -l ../log/checksums.log
    Check for errors
    $ cat ../log/task.log | grep -c ERROR
    Run next task
    $ time ./setFile.sh log/done-set-file-obj-ids.log
    How many have we done
    $ wc -l ../log/done-set-file-obj-ids.log
    Check for errors
    $ cat ../log/task.log | grep -c ERROR
    Run next task
    $ time ./verify.sh log/done-verify-obj-ids.log
    How many have we done
    $ wc -l ../log/done-verify-obj-ids.log
    Check for errors
    $ cat ../log/task.log | grep -c ERROR
    Run next task
    $ time ./setOther.sh easy-*:* log/done-set-other-obj-ids.log
    How many have we done
    $ wc -l ../log/done-set-other-obj-ids.log
    Check for errors
    $ cat ../log/task.log | grep -c ERROR

If anything goes wrong in setFile, you can speedup the resume by removing the lines from the checksums.log file that need no processing.
Backup a full version of the checksums.log file first!
Then use vi to find the last line that was OK, and remove the ones above.


## Performance

The task might degrade performance of easy. The file `cfg/application.conf`
contains some parameters make the task behave nicer.

* `fedora.request.interval.millis` sets the minumum time between completion
  of one request and the start of the next.
* `fedora.object.processor.chunk.size` determines the number of object id-s
  fetched in a single request. These request do not apply the interval parameter,
  but as each object causes at least one request per stream, that should not
  cause frequent quickly repeating requests if the chunk size is not chosen too small.

Note that if a value (like a password) contains a '$' character it needs to be double-quoted

## Log files

The log folder will be created implicitly in the working directory.
With the delivered configuration, scripts will write to the console
and `log/task.log`

```
calculate.sh -+-> log/checksums.log  -+-> setFiles.sh
              |                       |
              |                       +-> verify.sh
              |
              +-> log/stream-ids.log ---> setOther.sh
```

If `log/stream-ids.log` was not created due to interruption, it can be
created by filtering unique values from the last column of `log/checksums.log`
Unique values are not required, but `setOther.sh` will start quicker with the
actual processing, having an easier job to reduce the set to unique values.

## Known issue

When trying to activate the checksum for a DC stream fedore responds with
a 400-Bad-request: 'InputStream cannot be null'. We can skip these streams
(and other streams with problems not yet discoverd by tests)
by adding a line with "DC" to `log/stream-ids.log` before starting `setOther.sh`
and activate these checksums when we discover a solution.