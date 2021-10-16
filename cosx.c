#include<stdio.h>
#define pi 3.14159265358979
double cos(double x)
{
    int i;
    double r=1,s=1,y=1;
    while(x>2*pi) x-=2*pi;
    for(i=1;i<50;i+=2)
    {
        y*=-x*x;
        r*=i*(i+1);
        s+=y/r;
    }
    return s;
}
int main()
{
    double x;
    scanf("%lf",&x);
    printf("%lf\n",cos(x));
    return 0;
}