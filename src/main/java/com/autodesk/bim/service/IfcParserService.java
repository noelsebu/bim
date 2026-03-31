package com.autodesk.bim.service;

import com.autodesk.bim.domain.BimElement;
import com.autodesk.bim.domain.BimLevel;
import com.autodesk.bim.domain.BimProperty;
import com.autodesk.bim.exception.BimException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
import java.util.regex.*;

@Service
@Slf4j
public class IfcParserService {

    private static final Pattern ENTITY_PATTERN =
        Pattern.compile("^#(\d+)\s*=\s*(IFC\w+)\s*\((.*)\);?\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern SCHEMA_PATTERN =
        Pattern.compile("FILE_SCHEMA\s*\(\s*\('(\w+)'\s*\)\s*\)", Pattern.CASE_INSENSITIVE);

    private static final Set<String> SPATIAL_ELEMENTS = Set.of(
        "IFCWALL","IFCWALLSTANDARDCASE","IFCSLAB","IFCCOLUMN","IFCBEAM",
        "IFCDOOR","IFCWINDOW","IFCSTAIR","IFCROOF","IFCFURNISHINGELEMENT",
        "IFCFLOWSEGMENT","IFCPIPE","IFCDUCT","IFCSPACE","IFCZONE",
        "IFCPLATE","IFCMEMBER","IFCFOOTING","IFCPILE"
    );
