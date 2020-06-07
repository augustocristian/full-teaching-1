package com.fullteaching.e2e.no_elastest;

import org.junit.jupiter.api.Test;
import retorch.exceptions.NoFinalActivitiesException;
import retorch.exceptions.NoTGroupsInTheSchedulerException;
import retorch.exceptions.NotValidSystemException;
import retorch.exceptions.classifier.EmptyInputException;
import retorch.main.OrchestrationGenericToolBox;
import retorch.resourceidentification.RetorchSerializer;

import java.io.IOException;

public class dummyTestRetorch {
    @Test
    public void testRetorch () throws IOException, NoFinalActivitiesException, NotValidSystemException, EmptyInputException, NoTGroupsInTheSchedulerException, ClassNotFoundException {
        RetorchSerializer serializer = new RetorchSerializer();
        serializer = new RetorchSerializer();
        String databaseParent = "MySQL";
        String courseId = "Course";
        final String TYPE_LOGICAL = "LOGICAL";
        serializer.addResourceToSerialize(courseId, 100.0, 5, TYPE_LOGICAL, databaseParent);
        serializer.addResourceToSerialize("LoginService", 30.0, 5, TYPE_LOGICAL, databaseParent);
        serializer.addResourceToSerialize("Configuration", 15.0, 40, TYPE_LOGICAL, courseId);
        serializer.addResourceToSerialize("Information", 15.0, 20, TYPE_LOGICAL, courseId);
        serializer.addResourceToSerialize("Session", 15.0, 20, TYPE_LOGICAL, courseId);
        serializer.addResourceToSerialize("Forum", 15.0, 20, TYPE_LOGICAL, courseId);
        serializer.addResourceToSerialize("Files", 15.0, 20, TYPE_LOGICAL, courseId);
        serializer.addResourceToSerialize("Attenders", 15.0, 20, TYPE_LOGICAL, courseId);
        serializer.addResourceToSerialize("OpenVidu", 300.0, 1, TYPE_LOGICAL);
        serializer.addResourceToSerialize("OpenViduMock", 10.0, 30, TYPE_LOGICAL, "OpenVidu");


        serializer.serializeResources("EndToEndFullTeaching");

        OrchestrationGenericToolBox toolBox=  new OrchestrationGenericToolBox();
        toolBox.generateDockerFile("com.fullteaching.e2e.no_elastest.functional.test","EndToEndFullTeaching");



    }
}
