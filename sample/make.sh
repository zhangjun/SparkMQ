
# compile hiredis.so for python

mkdir -p obj
mkdir -p lib

gcc -pthread -fno-strict-aliasing -g -O2 -DNDEBUG -g -fwrapv -O3 -Wall -Wstrict-prototypes -fPIC -c vendor/hiredis/read.c -o build/obj/read.o
gcc -pthread -fno-strict-aliasing -g -O2 -DNDEBUG -g -fwrapv -O3 -Wall -Wstrict-prototypes -fPIC -c vendor/hiredis/sds.c -o build/obj/sds.o
ar rc build/obj/libhiredis_for_hiredis_py.a build/obj/read.o build/obj/sds.o

mkdir -p build/obj/src
gcc -pthread -fno-strict-aliasing -g -O2 -DNDEBUG -g -fwrapv -O3 -Wall -Wstrict-prototypes -fPIC -Ivendor -I/usr/include/python2.6 -c src/hiredis.c -o build/obj/src/hiredis.o
gcc -pthread -fno-strict-aliasing -g -O2 -DNDEBUG -g -fwrapv -O3 -Wall -Wstrict-prototypes -fPIC -Ivendor -I/usr/include/python2.6 -c src/reader.c -o build/obj/src/reader.o
gcc -pthread -shared build/obj/src/hiredis.o build/obj/src/reader.o -Lbuild/obj -lhiredis_for_hiredis_py -o build/lib/hiredis.so

cp -R hiredis ../redis-py/redis  
cp build/lib/hiredis.so ../redis-py/redis/hiredis
