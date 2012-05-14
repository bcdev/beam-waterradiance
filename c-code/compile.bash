
gcc -fPIC -O3 -funroll-loops -c Axb.c
gcc -fPIC -O3 -funroll-loops -c lmbc.c
gcc -fPIC -O3 -funroll-loops -c misc.c
gcc -fPIC -O3 -funroll-loops -c prepare_a_nn.c
gcc -fPIC -O3 -funroll-loops -c smile_table1_20110818_b.c
gcc -fPIC -O3 -funroll-loops -c use_the_nn.c
gcc -fPIC -O3 -funroll-loops -c levmar_nn_beam_20120117_m8_c.c
gcc -fPIC -O3 -funroll-loops -c beam_auxdata.c
gcc -shared -Wl,-soname,levmar4beam_dll.so -o liblevmar4beam_dll.so *.o -lc
