//Cache control

function getDayDifference(startDate) {
  today = new Date();
  newday = new Date(startDate);
  msPerDay = 24 * 60 * 60 * 1000 ;
  timeLeft = (newday.getTime() - today.getTime());
  e_daysLeft = timeLeft / msPerDay;
  daysLeft = Math.floor(e_daysLeft);
  return daysLeft < 0 ? ("<span style=\"color : red\">"+Math.abs(daysLeft).toString() + " days ago</span>") : (daysLeft.toString() + " days left");
}

