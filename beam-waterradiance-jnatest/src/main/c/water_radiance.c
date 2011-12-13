#include <stdlib.h>
#include <stdio.h>
#include <math.h>

__declspec(dllexport) int levmar_nn(double reflec[], int n_reflec, double iop[], int n_iop)
{
    double sum;
	int i;

	sum = 0;
    for (i = 0; i < n_reflec; i++) {
	    sum += reflec[i];
	}
    for (i = 0; i < n_iop; i++) {
	    iop[i] = (i + 1.0) * sum;
	}

    return n_iop;
}


int main(int argc, char** argv)
{
    int i;
	printf("Hello! We have %d args:\n", argc);
    for (i = 0; i < argc; i++) {
	   printf("%d: %s\n", i, argv[i]);
	}
    return 0;
}
