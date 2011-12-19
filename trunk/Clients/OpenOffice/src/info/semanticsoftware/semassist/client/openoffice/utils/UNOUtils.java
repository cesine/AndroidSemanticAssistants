/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info
Nikolaos Papadakis
Tom Gitzinger

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.semanticsoftware.semassist.client.openoffice.utils;

import java.io.*;

import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import com.sun.star.container.XEnumeration;
import com.sun.star.container.NoSuchElementException;

import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XController;
import com.sun.star.frame.XComponentLoader;

import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextRange;
import com.sun.star.text.XText;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;

import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.WrappedTargetException;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextField;
import com.sun.star.util.XSearchDescriptor;
import com.sun.star.util.XReplaceable;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.XMLElementModel;
import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;

public class UNOUtils
{
    private static final int HIGHLIGHT_YELLOW = 0x00FFFD00;
    private static final int HIGHLIGHT_OFF = 0xFFFFFF0A;
    private static int CURRENT_HIGHLIGHT = HIGHLIGHT_YELLOW;

    private static XMultiServiceFactory mxDocFactory = null;
    private static XTextCursor mxDocCursor = null;
    private static XSearchDescriptor mxSearchDescr = null;
   
    private static XReplaceable mxSearchable = null;  /* both for search & replace */
   
    private static XText mxAnnotText = null;
    private static String mCurrentPipeline;

    /**
     * Retrieves the URI of the current document.
     *
     * @param ctx CopenOffice context.
     * @return uri for the document if available, empty if document
     *             not saved or null on error.
     */
    public static URI getDocumentURI( final XComponentContext ctx )
    {
      URI result;
      try {
         final XModel model = getActiveDocumentModel(ctx);
         result = new URI(model.getURL());
      } catch (final URISyntaxException ex) {
         System.out.println("Error retrieving URI from document context.");
         result = null;
      }
      return result;
    }

    /**
     * Determines if a document is currently loaded.
     *
     * @param ctx OpenOffice context.
     * @param url String of the URL. Use empty string for documents that have
     *            not been saved.
     *
     * @return True if @a url is among the loaded documents, false otherwise.
     */
    public static boolean isDocumentLoaded(final XComponentContext ctx, final String url)
    {
      final XEnumeration docs = getDesktop(ctx).getComponents().createEnumeration();
      while (docs.hasMoreElements()) {
         try {
            final XModel elem =
               UnoRuntime.queryInterface(XModel.class, docs.nextElement());

            // Normalize excess slashes.
            final String normURL = elem.getURL().equals("") ? "" :
               new URI(elem.getURL()).toURL().toString();

            if (url.equals(normURL)) {
               return true;
            }               
         } catch (final com.sun.star.container.NoSuchElementException ex) {
            ex.printStackTrace(System.err);
         } catch (final com.sun.star.lang.WrappedTargetException ex) {
            ex.printStackTrace(System.err);
         } catch (final Exception ex) {
            System.err.println(ex.getMessage());
         }
      }
      return false;
   }

    /**
     * Retrieves either the marked text of the current document
     * or, if nothing is marked, the whole text of the document.
     */
    public static String getArgumentText( final XComponentContext ctx )
    {
        // Get the XModel interface from the active document,
        // and its controller
        XModel xModel = getActiveDocumentModel( ctx );
        XController xController = xModel.getCurrentController();

        // The controller gives us the TextViewCursor
        // Query the viewcursor supplier interface
        XTextViewCursorSupplier xViewCursorSupplier =
                                UnoRuntime.queryInterface(
                XTextViewCursorSupplier.class, xController );

        // Get the cursor
        XTextViewCursor xViewCursor = xViewCursorSupplier.getViewCursor();
        String result = xViewCursor.getString();

        if( result == null || result.equals( "" ) )
        {
            final XTextDocument xDoc = UnoRuntime.queryInterface( XTextDocument.class, xModel );
            XText wholeText = xDoc.getText();
            return wholeText.getString();
        }
        else
        {
            return result;
        }

    }


    /**
     * Spawns an OpenOffice window with a new document from a given text.
     *
     * @param ctx Context.
     * @param text Text content for the new document.
     */
    public static void createNewDoc( final XComponentContext ctx, final String text )
    {
        try {
            // Get an new empty document.
            final XTextDocument doc = UnoRuntime.queryInterface(
               com.sun.star.text.XTextDocument.class,
               loadDoc(ctx, "private:factory/swriter"));

            // Add text content to document.
            doc.getText().setString(text);
        } catch( final Exception ex ) {
            System.err.println("Failed to create textual document");
            ex.printStackTrace( System.err );
        }
    }


    /**
     * Spawns an OpenOffice window with a document from a given file.
     *
     * @param ctx Context.
     * @param file Local file from which to get content for new document.
     */
    public static void createNewDoc( final XComponentContext ctx, final File f )
    {
        // Create URL string from file object.
        String url;
        try {
            url = "file://" + f.getCanonicalPath();
        } catch( final Exception ex ) {
            System.out.println("WARNING: Corrupted URL, using empty instead.");
            url = "";
        }

        loadDoc(ctx, url);
    }

    /**
     * Helper method to spawn an OpenOffice window with a new or existing document.
     *
     * @param ctx Contex.
     * @param url URL of the existing document or protocol for the new.
     *
     * @return Component of the spawned frame if successful or null otherwise.
     */
    private static XComponent loadDoc(final XComponentContext ctx, final String url)
    {
      XComponent comp = null;

      // Query the XComponentLoader interface from the Desktop service
      final XComponentLoader xComponentLoader = UnoRuntime.queryInterface(
         XComponentLoader.class, getDesktop(ctx) );

      try {
         // Retrieve any existing frames with the same URL, 
         // else create new ones.
         comp = xComponentLoader.loadComponentFromURL(
            url, "_default", 0, new PropertyValue[0]);     //<<dbg _blank
      } catch( final com.sun.star.io.IOException ex ) {
         System.err.println("Invalid URL <"+ url +">"); 
         ex.printStackTrace();
      } catch( final com.sun.star.lang.IllegalArgumentException ex ) {
         System.err.println("Invalid properties");
         ex.printStackTrace();
      }
      return comp;
    }

    public static void createDocAnnotations( final XComponentContext ctx, final Annotation annotation )
    {
        // get the active document
        XTextDocument doc = getActiveTextDocument( ctx );

        mxDocFactory = UnoRuntime.queryInterface(
                XMultiServiceFactory.class, doc );

        createInvisibleCursor(annotation);
    }

    public static void initializeCursor( final XComponentContext ctx )
    {
        // get the active document
        XTextDocument doc = getActiveTextDocument( ctx );

        mxDocFactory = UnoRuntime.queryInterface(
                XMultiServiceFactory.class, doc );

        mxSearchable = UnoRuntime.queryInterface( XReplaceable.class, doc );

        mxSearchDescr = mxSearchable.createSearchDescriptor();
    }

    /**
     * @return the mCurrentPipeline
     */
    public static String getCurrentPipeline()
    {
        return mCurrentPipeline;
    }

    /**
     * @param aMCurrentPipeline the mCurrentPipeline to set
     */
    public static void setCurrentPipeline( final String aMCurrentPipeline )
    {
        mCurrentPipeline = aMCurrentPipeline;
    }

    /**
     * Get the currently active text document
     */
    private static XTextDocument getActiveTextDocument( final XComponentContext ctx )
    {
        XModel xDocModel = getActiveDocumentModel( ctx );
        return UnoRuntime.queryInterface( XTextDocument.class, xDocModel );
    }

    private static XModel getActiveDocumentModel( final XComponentContext ctx )
    {
        XComponent document = getCurrentComponent( ctx );

        return UnoRuntime.queryInterface( XModel.class, document );
    }

    private static XComponent getCurrentComponent( final XComponentContext ctx )
    {
        XDesktop xDesktop = getDesktop( ctx );
        XComponent document = xDesktop.getCurrentComponent();

        return document;
    }

    /**
     * Get the desktop service
     */
    private static XDesktop getDesktop( final XComponentContext ctx )
    {
        XMultiComponentFactory xmcf = ctx.getServiceManager();
        Object desktop = null;
        try
        {
            desktop = xmcf.createInstanceWithContext( "com.sun.star.frame.Desktop", ctx );
        }
        catch( com.sun.star.uno.Exception e )
        {
        }
        return UnoRuntime.queryInterface( com.sun.star.frame.XDesktop.class, desktop );
    }

    private static void annotateField( final Annotation annotation )
    {
        try {
            mxAnnotText.insertTextContent(mxDocCursor, makeAnnotation(annotation), false);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Highlight annotated field
        highlightField();
    }

    /**
     * Create an annotation (side-note) textfield object.
     *
     * @param Annotation Memory representation of the annotation.
     *
     * @throws Exception
     */
    private static XTextField makeAnnotation(final Annotation annotation)
      throws Exception
    {
      // Create a side-note object & get its properties to be modified.
      final XTextField annot = UnoRuntime.queryInterface(
         XTextField.class, mxDocFactory.createInstance(
         "com.sun.star.text.TextField.Annotation"));
      final XPropertySet props = UnoRuntime.queryInterface(XPropertySet.class, annot);

      // Text is for embedding complex objects into side-notes.
      final XText text = UnoRuntime.queryInterface(
         XText.class, props.getPropertyValue("TextRange"));
      text.insertString(text, "type= "+ annotation.mType +"\n", false);

      // Configure look-&-feel of side-note information.
      setFontSize(text, ClientPreferences.getSideNoteFontSize());

      // If configured, duplicate annotation content as part of the side-note.
      if (ClientPreferences.isShowAnnotationContent()) {
         text.insertString(text, "content= "+ annotation.mContent +"\n", false);
      }

      // Iterate through annotation features.
      final Set<String> keys = annotation.mFeatures.keySet();
      final Iterator<String> iter = keys.iterator();

      while (iter.hasNext()) {
         final String key = iter.next();
         final String val = annotation.mFeatures.get(key);

         // If configured, suppress empty-valued features.
         if (ClientPreferences.isEmptyFeatureFilter() && "".equals(val)) {
            System.out.println("---------------- Ignoring empty valued feature: "+ key);
            continue;
         }

         // Make URL features hyper-linkable.
         if ("url".equalsIgnoreCase(key)) {
            text.insertString(text, key +"= ", false);
            text.insertTextContent(text, makeHyperLink(val, val), false);
            text.insertString(text, "\n", false);
         } else {
            text.insertString(text, key +"= "+ val +"\n", false);
         }
      }

      // Define side-note properties.
      try {
         // NOTE: It is not clear from OpenOffice's API if the "Content" property
         // is strictly required, which are the default values, nor what are the
         // consequences of (not) having it.
         //props.setPropertyValue("Content", annot.getPresentation(false));
         props.setPropertyValue("Author", mCurrentPipeline);
      } catch (UnknownPropertyException e) {
         /* Thrown ONLY on programming/typo error of the try body! */
         e.printStackTrace();
      }

      return annot;
    }

    /**
     * Create a hyperlink textfield object.
     *
     * @param linkURL Address of the hyperlink including its protocol (ie: http://)
     * @param linkName Symbolic name of the hyperlink.
     *
     * @throws Exception
     */
    private static XTextField makeHyperLink(final String linkURL, final String linkName)
      throws Exception
    {
      //Create a URL object & gets its properties to be modified.
      final XTextField link = UnoRuntime.queryInterface(
         XTextField.class, mxDocFactory.createInstance(
         "com.sun.star.text.TextField.URL"));
      final XPropertySet props = UnoRuntime.queryInterface(XPropertySet.class, link);

      // Define URL properties.
      try {
         props.setPropertyValue("Representation", linkName);
         props.setPropertyValue("URL", linkURL);
      } catch (UnknownPropertyException e) {
         /* Thrown ONLY on programming/typo error of the try body! */
         e.printStackTrace();
      }

      return link;
    }

    /**
     * Configure the font-size of a range of text.
     *
     * @param range of text to change the font.
     * @param size value of the new font.
     */
    private static void setFontSize(final XTextRange range, final float size)
    {
        // Extract the cursor properties & change its font-size.
        final XTextCursor cursor = range.getText().createTextCursorByRange(range);
        final XPropertySet props = UnoRuntime.queryInterface(XPropertySet.class, cursor);
        try {
            props.setPropertyValue("CharHeight", size);
        } catch (UnknownPropertyException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (com.sun.star.lang.WrappedTargetException e) {
            e.printStackTrace();
        } catch (com.sun.star.lang.IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static void highlightField()
    {
        try
        {
            // Highlight text to yellow
            // call setPropertyValue, passing in a Float object
            // query the XPropertySet interface


            final XPropertySet xCursorProps = UnoRuntime.queryInterface( XPropertySet.class, mxDocCursor );
            xCursorProps.setPropertyValue( "CharBackColor", CURRENT_HIGHLIGHT );

            mxDocCursor.gotoRange( mxDocCursor.getEnd(), false );
            mxDocCursor.goRight( (short) 1, true );
            xCursorProps.setPropertyValue( "CharBackColor", 0xFFFFFF1A );
            //mxDocCursor.goLeft( (short) 1, false );

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

    }

    private static boolean isTextAnnotated(final XTextCursor cursor) {
      boolean result = false;
      final XPropertySet props = UnoRuntime.queryInterface(XPropertySet.class, cursor);
      try {
         final Object property = props.getPropertyValue("CharBackColor");
         result = (property.equals( HIGHLIGHT_OFF ) || property.equals( HIGHLIGHT_YELLOW ));
      } catch (final Exception ex) {
          ex.printStackTrace();
      }
      return result;
    }

    /**
     * @deprecated This method checks if the text at the current position of
     * the document cursor is highlighted. However, if it is not, this method
     * has the side effect of highlighting it.
     *
     * @see isTextAnnotated
     * @see highlightField 
     */
    @Deprecated
    private static boolean IsTextAnnotated()
    {
        try
        {
            /*
            Hashtable textFields;
            final XTextFieldsSupplier supplier = UnoRuntime.queryInterface( XTextFieldsSupplier.class, xTextDoc );
            XEnumerationAccess enumAccess = supplier.getTextFields();
            XEnumeration xEnum = enumAccess.createEnumeration();
            textFields = new Hashtable();

            while( xEnum.hasMoreElements() )
            {
            Object o = xEnum.nextElement();
            final XTextField text = UnoRuntime.queryInterface( XTextField.class, o );
            // text.getAnchor()
            //System.out.println( "---------------- XTextField text: " + text.getAnchor() );

            }
             */


            final XPropertySet xCursorProps = UnoRuntime.queryInterface( XPropertySet.class, mxDocCursor );

            Object property = xCursorProps.getPropertyValue( "CharBackColor" );


            boolean result = (property.equals( HIGHLIGHT_OFF ) || property.equals( HIGHLIGHT_YELLOW ));

            if( result )
            {
                if( !property.equals( CURRENT_HIGHLIGHT ) )
                {
                    highlightField();
                }
            }

            System.out.println( "---------------- result: " + result );

            return result;

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Searches the document for the occurrence of the annotation.
     *
     * @param annot Annotation to search in the document
     * @return TextRange of the occurring annotation in the document
     *         if it is found, null otherwise.
     *
     * @note The implemented annotation occurrence resolution strategy
     * simply relies on string matching. This will fail in cases when
     * a string pattern occurs multiple times in the document but not
     * all have the same annotation (if any).
     */
    private static XTextRange findAnnotation(final Annotation annot) {
      // Configure search settings.
      try {
         mxSearchDescr.setPropertyValue("SearchWords", Boolean.valueOf(true));
         mxSearchDescr.setPropertyValue("SearchCaseSensitive", Boolean.valueOf(true));
         mxSearchDescr.setSearchString(annot.mContent);
      } catch (final Exception ex) {
         // Interested in: UnknownProperty, WrappedTarget & IllegalArgument exceptions.
         System.err.println("Could not configure search.");
         ex.printStackTrace();
         return null;
      }

      // Do the search.
      Object search = null;
      try {
         search = mxSearchable.findFirst(mxSearchDescr);
      } catch (final com.sun.star.uno.RuntimeException ex) {
         System.out.println("No more annotations to search for.");
         return null;
      }

      /* NOTE: The following error-reduction strategy may not be needed
         nor suitable for all search usages.

      // Convert to proper type.
      final XTextRange result = UnoRuntime.queryInterface(XTextRange.class, search);
      if (result == null) {
         return null;
      }

      // Position cursor on annotation span.
      final XText annotTxt = result.getText();
      final XTextCursor cursor = annotTxt.createTextCursor();
      cursor.gotoRange(result, false);
      cursor.gotoRange(result, true);

      // Keep searching if any hits are not annotation instances.
      while (!isTextAnnotated(cursor)) {
         try {
            search = mxSearchable.findNext(search, mxSearchDescr);
         } catch (final com.sun.star.uno.RuntimeException ex) {
            System.out.println("No more annotations to search for.");
            break;
         }
      }
      */

      // Convert to proper type.
      return (search == null) ? null :
         UnoRuntime.queryInterface(XTextRange.class, search);
    }


    public static boolean replaceAnnotation(final Annotation annot, final String str) {
      // Search for the annotation.
      final XTextRange found = findAnnotation(annot);
      if (found == null) {
         System.err.println("Annotation not found in document.");
         return false;
      }

      // Position document cursor on searched annotation.
      final XText text = found.getText();
      final XTextCursor cursor = text.createTextCursor();
      cursor.gotoRange(found, false);
      cursor.gotoRange(found, true);

      // Replace its content.
      try {
         text.insertString(cursor, str, true);
      } catch (final Exception ex) {
         System.err.println("Could not replace annotation text in document.");
         return false;
      }
      return true;
    }

    private static void createInvisibleCursor(final Annotation annotation)
    {
        boolean isMoreElements = true;

        try
        {
            // For plain text
            mxSearchDescr.setSearchString( annotation.mContent );
            mxSearchDescr.setPropertyValue( "SearchWords", Boolean.valueOf( true ) );

            System.out.println( "---------------- Text to be searched: " + annotation.mContent );


            Object xTextRange = mxSearchable.findFirst( mxSearchDescr );
            XTextRange xTagTxtRange = UnoRuntime.queryInterface( XTextRange.class, xTextRange );

            while( true )
            {
                // find initial range for the searched word 
                try
                {
                    mxAnnotText = xTagTxtRange.getText();
                    mxDocCursor = mxAnnotText.createTextCursor();
                    mxDocCursor.gotoRange( xTagTxtRange, false );
                    mxDocCursor.gotoRange( xTagTxtRange, true );

                    break;
                }
                catch( RuntimeException re )
                {
                    System.out.println( "---------------- non plain text, go to next range1" );
                    try
                    {
                        xTextRange = mxSearchable.findNext( xTextRange, mxSearchDescr );
                        xTagTxtRange = UnoRuntime.queryInterface( XTextRange.class, xTextRange );
                    }
                    catch( RuntimeException runtimeException )
                    {
                        System.out.println( "---------------- No more elements" );
                        isMoreElements = false;
                        break;
                    }
                }
            }

            while( IsTextAnnotated() && isMoreElements )
            {

                if( (xTextRange = mxSearchable.findNext( xTextRange, mxSearchDescr )) == null )
                {
                    System.out.println( "---------------- No more elements" );
                    isMoreElements = false;
                    break;
                }

                xTagTxtRange = UnoRuntime.queryInterface( XTextRange.class, xTextRange );

                if( xTagTxtRange == null )
                {
                    System.out.println( "---------------- Null tagTxtRange " );
                }
                // go to start, the to the stat of the range and then expand accordingly
                try
                {
                    mxAnnotText = xTagTxtRange.getText();
                    mxDocCursor = mxAnnotText.createTextCursor();
                    mxDocCursor.gotoRange( xTagTxtRange, false );
                    mxDocCursor.gotoRange( xTagTxtRange, true );
                }
                catch( RuntimeException runExc )
                {
                    System.out.println( "---------------- non plain text, go to next range2" );
                }

            }

            if( isMoreElements )
            {
                // Annotate text field
                annotateField( annotation );
            }

        }
        catch( Exception e )
        {
            System.out.println( "---------------- Exception in plain text searching" );
            e.printStackTrace();
            return;
        }

    }

    // FIXME: Duplication from Eclipse Utils.java to be consolidated in CSAL
    // once all duplicated client ServiceAgentSingletons implementations are
    // refactored.
    public static void propertiesReader()
      throws NullPointerException {
		// Should return only one item in the list
	   ArrayList<XMLElementModel> server = ClientUtils.getClientPreference(ClientPreferences.CLIENT_NAME, "server");
		
   	// if there are no server defined for this client. then look for the last called one in the global scope
		if (server.size() == 0) {
	      server = ClientUtils.getClientPreference(ClientUtils.XML_CLIENT_GLOBAL, "lastCalledServer");
   	}
   	// Note that if the former case, if by mistake there are more than
      // one server defined, we pick the first one. If the specific host/port
      // attributes are not found, the preference file is corrupt &
      // implicitly throw an exception.
		ServiceAgentSingleton.setServerHost(server.get(0).getAttribute().get(ClientUtils.XML_HOST_KEY));
	   ServiceAgentSingleton.setServerPort(server.get(0).getAttribute().get(ClientUtils.XML_PORT_KEY));
    }
}
