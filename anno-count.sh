n=`grep -wo "@NonDet" $1 | wc -l`
o=`grep -wo "@OrderNonDet" $1 | wc -l`
d=`grep -wo "\@Det" $1 | wc -l`
p=`grep -wo "@PolyDet" $1 | wc -l`
a=`expr $n + $o + $d + $p`
echo "Number of annotations:" $a
echo "Annotated lines:"
grep -wr "@NonDet\|@OrderNonDet\|@Det\|@PolyDet" $1 | wc -l
echo "Unverified methods:"
grep -Ewr '@SuppressWarnings.*determinism' $1 | grep "determinism" | wc -l
