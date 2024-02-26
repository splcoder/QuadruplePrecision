#include "R_C_functions.h"

char RF::buf[ RF::BUF_SIZE ] = {'\0'};

vector<unique_ptr<char[]>> RF::vBuf = vector<unique_ptr<char[]>>();

size_t RF::ARRAY_SIZE = 0;

const char RF::EMPTY[ 1 ] = {'\0'};

const R RF::INF_P = (R)1/(R)0;
const R RF::INF_N = (R)-1/(R)0;
const R RF::NAN = nanq("");

std::ostream& operator<< ( std::ostream& out, const R& r ){
    char buf[ 128 ];
    int n = quadmath_snprintf( buf, sizeof buf, "%+-#46.*Qe", RF::MAX_DIGIT_PREC, r );
	if( (size_t) n < sizeof buf )	out << buf;
    else							out << "BUFFER_OVERFLOW";
    return out;
}
