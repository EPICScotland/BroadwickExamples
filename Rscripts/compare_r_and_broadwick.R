# compare Broadwick and R simulations for SIR
# S. J. Lycett
# 7 April 2014

# read in broadwick results
# BasicSIR
bpath <- "C://Users//Samantha Lycett//Documents//NetBeansProjects//BroadwickExamples//"
bname <- "basicsir_test.txt"
bdata <- read.table( paste(bpath,bname,sep=""), header=TRUE, sep=",")

# Individual based SIR
bname2 <- "individualsir_test_modelState.txt"
bdata2 <- read.table( paste(bpath,bname2,sep=""), header=TRUE, sep=",")

# read in R ODE
rpath <- "C://Users//Samantha Lycett//Documents//NetBeansProjects//compare_with_r//"
rname1<- "Rgenerated_SIR_with_ODE.csv"
rdata1<- read.table( paste(rpath,rname1,sep=""), header=TRUE, sep=",")

# read in approx R
rpath <- "C://Users//Samantha Lycett//Documents//NetBeansProjects//compare_with_r//"
rname2<- "Rgenerated_SIR.csv"
rdata2<- read.table( paste(rpath,rname2,sep=""), header=TRUE, sep=",", skip=6)

plot(  rdata1[,1], rdata1[,3], type="l", xlab="Time", ylab="Number I", main="SIR Models", ylim=c(0,500))
lines( rdata2[,1]-1, rdata2[,3], col="blue")
lines( bdata[,1], bdata[,3], col="red")
lines( bdata2[,1], bdata2[,3], col="magenta")
legend("topright", c("ODE","Approx ODE","BasicSIR","IndividualSIR"), lty=1, bty="n", col=c("black","blue","red","magenta"))

