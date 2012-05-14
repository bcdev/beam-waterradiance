#include <stdio.h>
#include <stdlib.h>
#include <string.h>


char* concat_path(const char* homeDir, const char* fileName) {
    char* auxPath = ".beam/beam-waterradiance-processor/auxdata";
    char *buf = (char*) malloc((strlen(homeDir) + strlen(auxPath) + strlen(fileName) + 3) * sizeof(char));

    strcpy (buf, homeDir);
    strcat (buf, "/");
    strcat (buf, auxPath);
    strcat (buf, "/");
    strcat (buf, fileName);
    return buf;
}

FILE* open_auxfile(const char* fileName) {
    char *path;
    char* home = getenv("HOME");
    char* mapred_home = "/home/mapred";
    FILE* fd;

    printf("Home: '%s'\n", home);
    path = concat_path(home, fileName);

    printf("Opening: '%s'\n", path);
    if((fd=fopen(path,"r"))==0){
        printf("Reading failed, will try calvalus path...\n", path);
        free(path);
        path = concat_path(mapred_home, fileName);
        printf("Opening: '%s'\n", path);
        if((fd=fopen(path,"r"))==0){
          printf("Reading failed again\n", path);
          exit(EXIT_FAILURE);
        }
    }
    free(path);
    return fd;
}
