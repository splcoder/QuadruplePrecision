#include "R_C_functions.h"

char RF::buf[ RF::BUF_SIZE ] = {'\0'};

vector<unique_ptr<char[]>> RF::vBuf = vector<unique_ptr<char[]>>();

size_t RF::ARRAY_SIZE = 0;

const char RF::EMPTY[ 1 ] = {'\0'};

const R RF::NAN = RF::nan();

