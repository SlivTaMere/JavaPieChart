# JavaPieChart

Draws pie chart looking like

<img src="https://s3.amazonaws.com/uploads.hipchat.com/170039/1222784/UktBGTZFXastfPK/upload.png" />

Instanciate jpc.JavaPieChart with a Map between the labels and the values.  
It extends JPanel to integrate it easily in Swing UI.  
See the render() method to write the pie chart to an image.

Try the example by running the main in jpc.ui.ChartMod

When used as a JPanel the pie chart can be modify at run time:
- Double-click to change the color of a slice.
- Drag and move top/bottom to rotate the chart.
- Drag and drop labels to move them.
- Double-click labels to edit them (the label becomes red). Hit escape when finished.


