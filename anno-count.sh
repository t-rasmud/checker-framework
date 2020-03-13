n=`grep -wr "@NonDet" $1 | wc -w`
o=`grep -wr "@OrderNonDet" $1 | wc -w`
d=`grep -wr "@Det" $1 | wc -w`
p=`grep -wr "@PolyDet" $1 | wc -w`
a=`expr $n + $o + $d + $p`
echo "Number of annotations:" $a
echo "Annotated lines:"
grep -wr "@NonDet\|@OrderNonDet\|@Det\|@PolyDet" $1 | wc -l
echo "Unverified methods:"
grep -wr "@SuppressWarnings" $1 | grep "determinism" | wc -l
