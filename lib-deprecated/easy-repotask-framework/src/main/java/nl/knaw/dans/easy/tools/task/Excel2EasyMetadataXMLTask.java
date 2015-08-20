package nl.knaw.dans.easy.tools.task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.dataset.AdministrativeMetadataImpl;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EasyMetadataFactory;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicDate;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.BasicRemark;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.InvalidDateStringException;
import nl.knaw.dans.pf.language.emd.types.IsoDate;
import nl.knaw.dans.pf.language.emd.types.Spatial;

import org.joda.time.DateTime;

public class Excel2EasyMetadataXMLTask extends AbstractTask {

    private String excelFilePath;

    private String originalFilesFolder;
    private String outputFolder;

    private String excelFileName;
    private String formatSelection;

    private String depositorId;
    private String archivistId;

    private Map<Integer, Column> columns = new HashMap<Integer, Column>();

    private Sheet sheet;

    private DatasetState datasetState;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        try {
            // Open workbook which is Windows CP-1252 encoded
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");

            Workbook workbook = Workbook.getWorkbook(new File(excelFilePath + excelFileName), ws);
            // get the first sheet of this excel file
            sheet = workbook.getSheet(0);

            RL.info(new Event("Workbook contains: " + sheet.getRows() + " rows"));

            // check which columns are in this sheet
            // configure expected columns
            Cell[] headers = sheet.getRow(0);
            String notExpected = "";
            for (int i = 0; i < headers.length; i++) {
                String value = headers[i].getContents();
                value.trim();
                if (!"".equals(value)) {
                    if (value.equalsIgnoreCase("title") || value.equalsIgnoreCase("titel")) {
                        columns.put(i, Column.TITLE);
                    } else if (value.equalsIgnoreCase("creator") || value.equalsIgnoreCase("auteur(s)")) {
                        columns.put(i, Column.CREATOR);
                    } else if (value.equalsIgnoreCase("date created iso8601") || value.equalsIgnoreCase("date_created")) {
                        columns.put(i, Column.DATE_CREATED_ISO8601);
                    } else if (value.equalsIgnoreCase("description") || value.equalsIgnoreCase("description abstract")) {
                        columns.put(i, Column.DESCRIPTION);
                    } else if (value.equalsIgnoreCase("access rights") || value.equalsIgnoreCase("access_rights")) {
                        columns.put(i, Column.RIGHTS_ACCESS);
                    } else if (value.equalsIgnoreCase("date available")) {
                        columns.put(i, Column.DATE_AVAILABLE);
                    } else if (value.equalsIgnoreCase("audience")) {
                        columns.put(i, Column.AUDIENCE);
                    } else if (value.equalsIgnoreCase("alternative title") || value.equalsIgnoreCase("alternative_title")) {
                        columns.put(i, Column.TITLE_ALT);
                    } else if (value.equalsIgnoreCase("date created")) {
                        columns.put(i, Column.DATE_CREATED);
                    } else if (value.equalsIgnoreCase("contributor")) {
                        columns.put(i, Column.CONTRIBUTOR);
                    } else if (value.equalsIgnoreCase("subject")) {
                        columns.put(i, Column.SUBJECT);
                    } else if (value.equalsIgnoreCase("spatial coverage") || value.equalsIgnoreCase("spatial_coverage")) {
                        columns.put(i, Column.COVERAGE_SPATIAL);
                    } else if (value.equalsIgnoreCase("spatial_point_x") || value.equalsIgnoreCase("spatial point x")) {
                        columns.put(i, Column.COVERAGE_SPATIAL_POINT_X);
                    } else if (value.equalsIgnoreCase("spatial_point_y") || value.equalsIgnoreCase("spatial point y")) {
                        columns.put(i, Column.COVERAGE_SPATIAL_POINT_Y);
                    } else if (value.equalsIgnoreCase("temporal coverage") || value.equalsIgnoreCase("temporal_coverage")) {
                        columns.put(i, Column.COVERAGE_TEMPORAL);
                    } else if (value.equalsIgnoreCase("source")) {
                        columns.put(i, Column.SOURCE);
                    } else if (value.equalsIgnoreCase("format")) {
                        columns.put(i, Column.FORMAT);
                    } else if (value.equalsIgnoreCase("language")) {
                        columns.put(i, Column.LANGUAGE);
                    } else if (value.equalsIgnoreCase("identifier")) {
                        columns.put(i, Column.IDENTIFIER);
                    } else if (value.equalsIgnoreCase("remarks")) {
                        columns.put(i, Column.OTHER_REMARKS);
                    }

                    else if (value.equalsIgnoreCase("om-nr.") || value.equalsIgnoreCase("archis") || value.equalsIgnoreCase("archis id")
                            || value.equalsIgnoreCase("Identifier (Archis onderzoek)") || value.equalsIgnoreCase("archis_onderzoeksmeldingsnr"))
                    {
                        columns.put(i, Column.ID_ARCHIS);
                    } else if (value.equalsIgnoreCase("(copy)right holder") || value.equalsIgnoreCase("rights_holder")) {
                        columns.put(i, Column.RIGHTS_HOLDER);
                    } else if (value.equalsIgnoreCase("publisher")) {
                        columns.put(i, Column.PUBLISHER);
                    } else if (value.equalsIgnoreCase("type")) {
                        columns.put(i, Column.TYPE);
                    }

                    else if (value.equalsIgnoreCase("filename") || value.equalsIgnoreCase("file_name")) {
                        columns.put(i, Column.FILENAME);
                    }

                    else {
                        notExpected += "[" + i + "]" + value + ",";
                    }
                }
            }
            if (notExpected.length() > 0) {
                throw new TaskException("The value(s){" + notExpected + "} cannot be mapped to a EasyMetadata-field", this);
            }
            System.out.println("Total filled in columns: " + columns.size() + "/" + headers.length);

            if (requiredColumsFound() && null != formatSelection) {
                MetadataFormat format = null;
                if (formatSelection.equals("ARCHAEOLOGY")) {
                    format = MetadataFormat.ARCHAEOLOGY;
                } else if (formatSelection.equals("HISTORY")) {
                    format = MetadataFormat.HISTORY;
                } else if (formatSelection.equals("SOCIOLOGY")) {
                    format = MetadataFormat.SOCIOLOGY;
                }

                if (null != format) {
                    processSheet(format);
                } else {
                    // No correct format was select! quit!
                    throw new TaskException("No MetadataFormat was selected.", this);
                }
            }
        }
        catch (BiffException e1) {
            throw new TaskException("The file [" + excelFileName + "] could not be read. Please check if it is a valid MS Excel File", this);
        }
        catch (IOException e1) {
            throw new TaskException("The file [" + excelFileName + "] does not exist. Please check if you've entered the correct filename and path["
                    + excelFilePath + "]", this);
        }
    }

    private void processSheet(MetadataFormat format) throws FatalTaskException {
        // for every row in the sheet
        // start with the second row...
        for (int i = 1; i < sheet.getRows(); i++) {
            // Get all the cells in this row
            Cell[] row = sheet.getRow(i);

            // Create empty EasyMetadata
            EasyMetadata emd = EasyMetadataFactory.newEasyMetadata(format);
            ArrayList<File> fileList = new ArrayList<File>(3);

            // Add the fields
            for (int index = 0; index < row.length; index++) {
                if (columns.containsKey(index)) {
                    switch (columns.get(index)) {
                    /** Required Fields **/
                    case TITLE:
                        addTitle(row[index], emd);
                        break;
                    case CREATOR:
                        addCreator(row[index], emd);
                        break;
                    case DATE_CREATED_ISO8601:
                        addISODateCreated(row[index], emd);
                        break;
                    case DESCRIPTION:
                        addDescription(row[index], emd);
                        break;
                    case RIGHTS_ACCESS:
                        addAccessRights(row[index], emd);
                        break;
                    case DATE_AVAILABLE:
                        addDateAvailable(row[index], emd);
                        break;
                    case AUDIENCE:
                        addAudiences(row[index], emd);
                        break;

                    case TITLE_ALT:
                        addAltTitle(row[index], emd);
                        break;
                    case DATE_CREATED:
                        addDateCreated(row[index], emd);
                        break;
                    case CONTRIBUTOR:
                        addContributor(row[index], emd);
                        break;
                    case SUBJECT:
                        addSubject(row[index], emd);
                        break;
                    case COVERAGE_SPATIAL:
                        addSpatialCoverage(row[index], emd);
                        break;
                    case COVERAGE_SPATIAL_POINT_X:
                        addSpatialCoveragePoints(row[index], row[index + 1], emd);
                        break;
                    case COVERAGE_TEMPORAL:
                        addTemporalCoverage(row[index], emd);
                        break;
                    case SOURCE:
                        addSource(row[index], emd);
                        break;
                    case FORMAT:
                        addFormat(row[index], emd);
                        break;
                    case LANGUAGE:
                        addLanguage(row[index], emd);
                        break;
                    case IDENTIFIER:
                        addIdentifiers(row[index], emd);
                        break;
                    case OTHER_REMARKS:
                        addRemarks(row[index], emd);
                        break;
                    /** Archaeology - Additional Fields **/
                    case ID_ARCHIS:
                        addArchisID(row[index], emd);
                        break;
                    case RIGHTS_HOLDER:
                        addRightsHolder(row[index], emd);
                        break;
                    case PUBLISHER:
                        addPublisher(row[index], emd);
                        break;
                    case TYPE:
                        addType(row[index], emd);
                        break;

                    /** Extra Field **/
                    case FILENAME:
                        addFiles(row[index], fileList);
                        break;

                    default:
                        break;
                    }
                } // skip the column
            }

            if (checkRequiredFields(emd)) {
                // Create Directory Structure
                /**
                 * <identifier>/metadata/ <identifier>/filedata/original/ Files: <identifier>/metadata/metadata.xml
                 * <identifier>/filedata/original/<filename.ext>
                 */
                String folderId = "[" + i + "]-" + ((emd.getPreferredTitle().substring(0, 15)).trim()).replaceAll(" ", "_");
                String metadataFolder = getOutputFolder() + "/" + folderId + "/metadata";
                String filedataFolder = getOutputFolder() + "/" + folderId + "/filedata/original";

                File metadata = new File(metadataFolder);

                if (metadata.mkdirs())
                    ;
                RL.info(new Event("created folder: " + metadata.getAbsolutePath()));
                File filedata = new File(filedataFolder);
                if (filedata.mkdirs())
                    RL.info(new Event("created folder: " + filedata.getAbsolutePath()));

                // Output XML
                OutputStream out = null;
                try {
                    // String xml = (String)
                    // JiBXObjectFactory.getJiBXUtil(EasyMetadataImpl.class).marshalDocument(emd, 4);
                    // System.out.println("Printing XML:");
                    // System.out.println(xml);
                    // Write the EMD to metadata.xml -file
                    out = new BufferedOutputStream(new FileOutputStream(new File(metadata.getAbsolutePath() + "/easymetadata.xml")));
                    new EmdMarshaller(emd).write(out);
                    RL.info(new Event("Copying metadata to: " + metadata.getAbsolutePath() + "/metadata.xml"));
                }
                catch (FileNotFoundException e) {
                    throw new TaskException(e, this);
                }
                catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e) {
                    throw new TaskException(e, this);
                }
                finally {
                    try {
                        if (out != null)
                            out.close();
                    }
                    catch (IOException e) {
                        throw new FatalTaskException(e, this);
                    }
                }

                completeAdministrativeSteps(metadataFolder, getDepositorId(), getArchivistId());

                // Copy Files
                for (File file : fileList) {
                    try {
                        copy(file, new File(filedata.getAbsolutePath() + "/" + file.getName()));
                    }
                    catch (IOException e) {
                        throw new TaskException(e, this);
                    }
                }
            } else {
                throw new TaskException("Row[" + i + "] doesn't contain all required fields. ", this);
            }
        }
    }

    private void completeAdministrativeSteps(String metadataFolder, String depositorId, String archivistId) throws TaskException {
        File admindata = new File(metadataFolder);
        if (admindata.mkdirs())
            ;
        RL.info(new Event("created folder: " + admindata.getAbsolutePath()));

        AdministrativeMetadata amd = new AdministrativeMetadataImpl();
        amd.setAdministrativeState(getDatasetState());
        amd.setDepositorId(depositorId);
        amd.getWorkflowData().setAssigneeId(archivistId);

        ArrayList<WorkflowStep> steps = (ArrayList<WorkflowStep>) amd.getWorkflowData().getWorkflow().getSteps();
        for (WorkflowStep step : steps) {
            if (step.getId().contains("sip") || step.getId().contains("dip")) {
                List<WorkflowStep> requests = step.getRequiredSteps();
                for (WorkflowStep req : requests) {
                    req.setCompleted(true, archivistId);
                }
            }
        }

        try {
            // String xml = (String)
            // JiBXObjectFactory.getJiBXUtil(AdministrativeMetadataImpl.class).marshalDocument(amd, 4);
            // System.out.println("Printing XML:");
            // System.out.println(xml);
            // Write the AMD to administrative-metadata.xml -file
            amd.serializeTo(new File(admindata.getAbsolutePath() + "/administrative-metadata.xml"), 4);
            RL.info(new Event("Copying metadata to: " + admindata.getAbsolutePath() + "/administrative-metadata.xml"));
        }
        catch (XMLSerializationException e) {
            throw new TaskException(e, this);
        }
    }

    private void copy(File src, File dest) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);

        byte[] buffer = new byte[1024];

        int length;
        // copy the file content in bytes
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }

        in.close();
        out.close();
        RL.info(new Event("File copied from " + src + " to " + dest));
    }

    private boolean requiredColumsFound() {
        boolean required = false;

        boolean title = false;
        boolean creator = false;
        boolean description = false;
        boolean dateCreated = false;
        boolean rights = false;
        // boolean dateAvailable = false;
        // boolean audience = false;

        for (int i = 0; i < columns.size(); i++) {
            switch (columns.get(i)) {
            case TITLE:
                title = true;
                break;
            case CREATOR:
                creator = true;
                break;
            case DATE_CREATED_ISO8601:
                dateCreated = true;
                break;
            case DESCRIPTION:
                description = true;
                break;
            case RIGHTS_ACCESS:
                rights = true;
                break;
            // case DATE_AVAILABLE: dateAvailable = true; break;
            // case AUDIENCE: audience = true; break;

            default:
                break;
            }
        }

        required = title && creator && dateCreated && description && rights; // && dateAvailable; //&&
                                                                             // audience;

        if (!required) {
            RL.error(new Event("Not all columns where present in the MS Excel file."));

            String missing = "[";
            if (!title)
                missing += "Title, ";
            if (!creator)
                missing += "Creator, ";
            if (!description)
                missing += "Description, ";
            if (!dateCreated)
                missing += "Date Created, ";
            if (!rights)
                missing += "Access Rights, ";
            // if(!dateAvailable)
            // missing += "Date Available, ";
            // if(!audience)
            // System.err.println("- Audience");
            RL.error(new Event("Missing:", missing + "]"));
        }

        return required;
    }

    private boolean checkRequiredFields(EasyMetadata emd) {
        /**
         * Required Fields TITLE, CREATOR, DATE_CREATED_ISO8601, DESCRIPTION, RIGHTS_ACCESS, DATE_AVAILABLE, - if not present set to today's date? AUDIENCE -
         * can be multiple, for archaeology it is not required, because it should be set to the default
         **/
        boolean title = !"".equalsIgnoreCase(emd.getEmdTitle().getPreferredTitle());
        boolean creator = emd.getEmdCreator().getDcCreator().size() > 0 && !"".equals(emd.getEmdCreator().getDcCreator().get(0));
        boolean dateCreated = emd.getEmdDate().getTermsCreated().size() > 0 && !"".equals(emd.getEmdDate().getTermsCreated().get(0));
        boolean description = emd.getEmdDescription().getDcDescription().size() == 1 && !"".equals(emd.getEmdDescription().getDcDescription().get(0));
        boolean accessRights = emd.getEmdRights().getAccessCategory() != null && !"".equals(emd.getEmdRights().getAccessCategory().name());
        boolean dateAvailable = emd.getEmdDate().getEasAvailable().size() == 1 && !"".equals(emd.getEmdDate().getEasAvailable().get(0));
        boolean audience = emd.getEmdAudience().getTermsAudience().size() > 0 && !"".equals(emd.getEmdAudience().getTermsAudience().get(0));

        if (!dateCreated) {
            emd.getEmdDate().getTermsCreated().add(new BasicDate(new DateTime().toString("yyyy-MM-dd")));
            dateCreated = true;
        }
        if (!dateAvailable) {
            emd.getEmdDate().getEasAvailable().add(new IsoDate(new DateTime().toString("yyyy-MM-dd")));
            dateAvailable = true;
        }
        if (!audience) {
            BasicString discipline = new BasicString("easy-discipline:2");
            discipline.setSchemeId("custom.disciplines");
            emd.getEmdAudience().getTermsAudience().add(discipline);
            audience = true;
        }

        boolean required = title && creator && dateCreated && description && accessRights && dateAvailable && audience;

        if (!required) {
            String missing = "[";
            if (!title)
                missing += "Title, ";
            if (!creator)
                missing += "Creator, ";
            if (!description)
                missing += "Description, ";
            if (!dateCreated)
                missing += "Date Created, ";
            if (!accessRights)
                missing += "Access Rights, ";
            if (!dateAvailable)
                missing += "Date Available, ";
            if (!audience)
                missing += "Auidience, ";
            RL.error(new Event("Missing:", missing.substring(0, missing.lastIndexOf(",")) + "]"));
        }
        return required;
    }

    private void addFiles(Cell cell, ArrayList<File> fileList) {
        String files = cell.getContents().trim();
        if (!"".equals(files)) {
            String[] items = files.split(";");
            if (!files.contains(";")) {
                items = files.split("\n");
            }

            for (String item : items) {
                File file = new File(originalFilesFolder + item);
                if (file.exists()) {
                    fileList.add(file);
                } else {
                    RL.warn(new Event("The File[" + file.getAbsolutePath() + "] does not exist!"));
                }
            }
        }
    }

    private void addIdentifiers(Cell cell, EasyMetadata emd) {
        String ids = cell.getContents().trim();
        if (!"".equals(ids)) {
            String[] items = ids.split(";");
            if (!ids.contains(";")) {
                items = ids.split("\n");
            }

            for (String item : items) {
                if (item.contains("eDNA-project")) {
                    String ednaId = item.substring(0, item.indexOf("(")).trim();
                    BasicIdentifier bi = new BasicIdentifier(ednaId);
                    bi.setScheme("eDNA-project");
                    emd.getEmdIdentifier().add(bi);
                } else {
                    emd.getEmdIdentifier().add(new BasicIdentifier(item.trim()));
                }
            }
        }
    }

    private void addTitle(Cell cell, EasyMetadata emd) {
        emd.getEmdTitle().getDcTitle().add(new BasicString(cell.getContents()));
    }

    private void addAltTitle(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdTitle().getTermsAlternative(), cell.getContents(), ";");
    }

    private void addCreator(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdCreator().getDcCreator(), cell.getContents(), ";");
    }

    private void addSubject(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdSubject().getDcSubject(), cell.getContents(), ";");
    }

    private void addDescription(Cell cell, EasyMetadata emd) {
        emd.getEmdDescription().getDcDescription().add(new BasicString(cell.getContents()));
    }

    private void addPublisher(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdPublisher().getDcPublisher(), cell.getContents(), ";");
    }

    private void addContributor(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdContributor().getDcContributor(), cell.getContents(), ";");
    }

    private void addISODateCreated(Cell cell, EasyMetadata emd) throws InvalidDateStringException {
        emd.getEmdDate().getEasCreated().add(new IsoDate(fixDateCreated(cell.getContents())));
    }

    private void addDateCreated(Cell cell, EasyMetadata emd) {
        emd.getEmdDate().getTermsCreated().add(new BasicDate(cell.getContents()));
    }

    private void addDateAvailable(Cell cell, EasyMetadata emd) {
        emd.getEmdDate().getEasAvailable().add(new IsoDate(cell.getContents()));
    }

    private void addAccessRights(Cell cell, EasyMetadata emd) {
        String rightsValue = cell.getContents();
        emd.getEmdRights().getTermsAccessRights().clear();
        // Set the Rights
        BasicString dcRights = new BasicString(AccessCategory.NO_ACCESS.name());
        if (rightsValue.contains("open") || AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS.name().equalsIgnoreCase(rightsValue)) {
            dcRights = new BasicString(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS.name());
        } else if (rightsValue.contains("permission") || AccessCategory.REQUEST_PERMISSION.name().equalsIgnoreCase(rightsValue)) {
            dcRights = new BasicString(AccessCategory.REQUEST_PERMISSION.name());

        } else if (rightsValue.contains("group") || AccessCategory.GROUP_ACCESS.name().equalsIgnoreCase(rightsValue)) {
            dcRights = new BasicString(AccessCategory.GROUP_ACCESS.name());
        } else if (rightsValue.contains("other") || AccessCategory.NO_ACCESS.name().equalsIgnoreCase(rightsValue)) {
            dcRights = new BasicString(AccessCategory.NO_ACCESS.name());
        } else {
            RL.error(new Event("AccessCategory [" + rightsValue + "] cannot be mapped! Defaulting to [NO_ACCESS]"));
        }

        // TODO: other schemeId for Archeology, History, ...?
        dcRights.setSchemeId("common.dcterms.accessrights");
        emd.getEmdRights().getTermsAccessRights().add(dcRights);

        // Accept the license
        emd.getEmdRights().getTermsLicense().add(new BasicString("accept"));
    }

    private void addAudiences(Cell cell, EasyMetadata emd) {
        // Set the Audience
        BasicString audience1 = new BasicString("easy-discipline:2"); // Default for Archaeology
        audience1.setSchemeId("custom.disciplines");
        emd.getEmdAudience().getTermsAudience().add(audience1);
    }

    private void addType(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdType().getDcType(), cell.getContents(), ";");
    }

    private void addFormat(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdFormat().getDcFormat(), cell.getContents(), ";");
    }

    private void addArchisID(Cell cell, EasyMetadata emd) {
        BasicIdentifier archisId = new BasicIdentifier(cell.getContents());
        archisId.setScheme("Archis_onderzoek_m_nr");

        emd.getEmdIdentifier().add(archisId);
    }

    private void addSource(Cell cell, EasyMetadata emd) {
        emd.getEmdSource().getDcSource().add(new BasicIdentifier(cell.getContents()));
    }

    private void addLanguage(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdLanguage().getDcLanguage(), cell.getContents(), ";");
    }

    private void addSpatialCoverage(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdCoverage().getTermsSpatial(), cell.getContents(), ";");
    }

    private void addSpatialCoveragePoints(Cell cellX, Cell cellY, EasyMetadata emd) {
        Spatial sp = new Spatial();
        Spatial.Point point = new Spatial.Point("RD", cellX.getContents(), cellY.getContents());
        point.setSchemeId("archaeology.eas.spatial");
        sp.setPoint(point);
        emd.getEmdCoverage().getEasSpatial().add(sp);
    }

    private void addTemporalCoverage(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdCoverage().getTermsTemporal(), cell.getContents(), ";");
    }

    private void addRightsHolder(Cell cell, EasyMetadata emd) {
        addSeperatedList((ArrayList<BasicString>) emd.getEmdRights().getTermsRightsHolder(), cell.getContents(), ";");
    }

    private void addRemarks(Cell cell, EasyMetadata emd) {
        emd.getEmdOther().getEasRemarks().add(new BasicRemark(cell.getContents()));
    }

    @SuppressWarnings("unused")
    private void addExtras(EasyMetadata emd) {
        // TODO: add extra's
    }

    public String fixDateCreated(String date) throws InvalidDateStringException {
        String[] split = date.split("-");

        String isodate = "";

        if (date.length() == 4) {
            isodate = date;
        } else if (split.length == 2) {
            if (split[0].length() == 4 && split[1].length() == 2)
                isodate = date;
            if (split[1].length() == 4 && split[0].length() == 2)
                isodate = split[1] + "-" + split[0];
        } else if (split.length == 3) {
            if (split[0].length() == 4 && split[1].length() == 2 && split[2].length() == 2)
                isodate = date;
            if (split[2].length() == 4 && split[1].length() == 2 && split[0].length() == 2)
                isodate = split[2] + "-" + split[1] + "-" + split[0];
        } else {
            throw new InvalidDateStringException("Date created not correct!: " + date);
        }
        return isodate;
    }

    public void addSeperatedList(ArrayList<BasicString> emdfield, String csvlist, String seperator) {
        String fallback = "\n";

        if (!"".equals(csvlist.trim())) {
            String[] items = csvlist.split(seperator);
            if (!csvlist.contains(seperator)) {
                items = csvlist.split(fallback);
            }

            for (String item : items) {
                emdfield.add(new BasicString(item.trim()));
            }
        }
    }

    public void setExcelFilePath(String excelFilePath) {
        this.excelFilePath = excelFilePath;
    }

    public String getExcelFilePath() {
        return excelFilePath;
    }

    public void setExcelFileName(String excelFileName) {
        this.excelFileName = excelFileName;
    }

    public String getExcelFileName() {
        return excelFileName;
    }

    public void setFormatSelection(String formatSelection) {
        this.formatSelection = formatSelection;
    }

    public String getFormatSelection() {
        return formatSelection;
    }

    public void setOriginalFilesFolder(String originalFilesFolder) {
        this.originalFilesFolder = originalFilesFolder;
    }

    public String getOriginalFilesFolder() {
        return originalFilesFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder + new DateTime().toString("yyyy-MM-dd-[HH.mm.ss]");
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public String getDepositorId() {
        return depositorId;
    }

    public void setDepositorId(String depositorId) {
        this.depositorId = depositorId;
    }

    public String getArchivistId() {
        return archivistId;
    }

    public void setArchivistId(String archivistId) {
        this.archivistId = archivistId;
    }

    public void setDatasetState(DatasetState datasetState) {
        this.datasetState = datasetState;
    }

    public DatasetState getDatasetState() {
        return datasetState;
    }

    /**
     * Don't use magic numbers, but this enum to get the values from a given column row The index represents the column in which the metadata value is stored.
     */
    public enum Column {
        /** Required Fields **/
        TITLE, CREATOR, DATE_CREATED_ISO8601, DESCRIPTION, RIGHTS_ACCESS, DATE_AVAILABLE, AUDIENCE,

        /** Additional Fields **/
        TITLE_ALT, DATE_CREATED, CONTRIBUTOR, SUBJECT, COVERAGE_SPATIAL, COVERAGE_SPATIAL_POINT_X, COVERAGE_SPATIAL_POINT_Y, COVERAGE_TEMPORAL, SOURCE, FORMAT, LANGUAGE, IDENTIFIER, OTHER_REMARKS,

        /** Archaeology - Additional Fields **/
        ID_ARCHIS, RIGHTS_HOLDER, PUBLISHER, TYPE,

        /** Extra field **/
        FILENAME;
    }
}
